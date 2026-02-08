package com.jcg.hibernate.maven;

import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import com.jcg.hibernate.maven.model.User;

public class TestHSession {

	public static void main(String[] art) {
		System.out.println("xx");
	}

	public static void test1() {
		// แบบปกติ
		Session sessionObj = null;
		User userObj = null;
		System.out.println(".......Hibernate Maven Example.......\n");
		try {

			// === Since Hibernate Version 5.6.x
			ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml")
					.build();
			SessionFactory sessionFactoryObj = new Configuration().buildSessionFactory(serviceRegistryObj);// แบบนี้ก็ได้
			sessionObj = sessionFactoryObj.openSession();
			sessionObj.beginTransaction();

			for (int i = 101; i <= 105; i++) {
				userObj = new User();
				userObj.setUserid(i);
				userObj.setUsername("Editor " + i);
				userObj.setCreatedBy("Administrator");
				userObj.setCreatedDate(java.time.OffsetDateTime.now());

				sessionObj.save(userObj);
			}
			System.out.println("\n.......Records Saved Successfully To The Database.......\n");

			// Committing The Transactions To The Database
			sessionObj.getTransaction().commit();
		} catch (Exception sqlException) {
			if (null != sessionObj.getTransaction()) {
				System.out.println("\n.......Transaction Is Being Rolled Back.......");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}
	}

	public static void testInsert() throws Exception {
		// ทดสอบทำเป็น class เอง

		// ทดสอบ insert
		try (HSession hss = HSession.sessionDb();) {
			hss.begintrans();

			for (int i = 101; i <= 105; i++) {
				User userObj = new User();
				userObj.setUserid(i);
				userObj.setUsername("Editor " + i);
				userObj.setCreatedBy("Administrator");
				userObj.setCreatedDate(java.time.OffsetDateTime.now());

				hss.persist(userObj);
			}
			
			System.out.println("\n.......Records Saved Successfully To The Database.......\n");
			
			testReadInTrans(hss);//อ่านมาแสดงก่อน commite ไม่ได้  //test 28/6/66
			
			hss.commit();
		}

	}

	public static void testReadInTrans(HSession hss) throws Exception {
		System.out.println("\n........test read before commit........\n");
		try (Stream<?> rst = hss.getSession().createQuery("from User").getResultStream();) {
			rst.forEach(ebj -> {
				User e = (User) ebj;
				System.out.println(e.getUserid() + " : " + e.getUsername());
			});
		}
	}
	
	public static void testRead() throws Exception {
		// ทดสอบอ่านข้อมูล
		try (HSession hss = HSession.sessionDb();) {

			// test HQL/JPQL ผ่านแล้ว
			try (Stream<?> rst = hss.getSession().createQuery("from User").getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					System.out.println(e.getUserid() + " : " + e.getUsername());
				});
			}

		}
		
		try (HSession hss = HSession.sessionDb();) {

			// test native query ผ่านแล้ว
			try (Stream<?> rst = hss.getSession().createNativeQuery("select * from user_table").getResultStream();) {
				rst.forEach(ebj -> {
					Object[] e = (Object[]) ebj;
					System.out.println(e[0] + " : " + e[1]);
				});
			}

		}

	}
	
	public static void testUpdate() throws Exception {
		// ทดสอบปรับปรุงข้อมูล
		try (HSession hss = HSession.sessionDb();) {
			hss.begintrans();

			//แบบที่ 1  ผ่านแล้ว
			User user =hss.getSession().find(User.class, 102);
			user.setUsername("user 102x");
			hss.save(user);
			
			//แบบที่ 2
			// test HQL/JPQL ผ่านแล้ว
			Query<?> qr1 = hss.getSession().createQuery("from User where userid =:userid ");
			qr1.setParameter("userid", 101);
			try (Stream<?> rst = qr1.getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					//e.setUserid(1000);
					e.setUsername("user 101x");
					hss.save(e);
					
				});
			}
			
			hss.commit();
		}

	}

}