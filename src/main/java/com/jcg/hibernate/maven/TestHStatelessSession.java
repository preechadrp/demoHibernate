package com.jcg.hibernate.maven;

import java.util.Date;
import java.util.stream.Stream;
import java.util.Set;

import javax.persistence.Tuple;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;

import com.jcg.hibernate.maven.model.User;

public class TestHStatelessSession {

	public static void main(String[] args) {
		System.out.println("demoHibernate");
	}

	public static void testInsert() throws Exception {
		// ทดสอบทำเป็น class เอง
		System.out.println("======testInsert()");
		

		
		// ทดสอบ insert
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			 // สร้าง ValidatorFactory และ Validator
	        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	        Validator validator = factory.getValidator();

			for (int i = 301; i <= 305; i++) {
				User userObj = new User()
						.setUserid(i)
						.setUsername("Editor " + i)
						.setCreatedBy("Administrator")
						.setCreatedDate(new Date());

				// ตรวจสอบวัตถุ
		        Set<ConstraintViolation<User>> violations = validator.validate(userObj);
		        
		        // แสดงผลข้อผิดพลาด
		        if (!violations.isEmpty()) {
		            for (ConstraintViolation<User> violation : violations) {
		                System.out.println("Validate Msg : " + violation.getMessage());
		            }
		        } else {
		            System.out.println("ข้อมูลถูกต้อง");
		            hss.insert(userObj);// จะเป็นการ insert ลงฐานจริงๆ แต่กรณีนี้อยู่ในการทำ begintransaction
		        }
				
			}

			System.out.println("\n.......Records Saved Successfully To The Database.......\n");

			testReadInTrans(hss);// อ่านมาแสดงก่อน commite ได้เมื่อใช้ StatelessSession //test ok 29/6/66

			hss.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testReadInTrans(HStatelessSession hss) throws Exception {
		System.out.println("\n........test read before commit........\n");
		try (Stream<?> rst = hss.getSession().createQuery("from User").getResultStream();) {
			rst.forEach(ebj -> {
				User e = (User) ebj;
				System.out.println(e.getUserid() + " : " + e.getUsername());
			});
		}
	}

	public static void testRead() throws Exception {
		System.out.println("======testRead()");
		// ทดสอบอ่านข้อมูล
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {

			// test HQL/JPQL ผ่านแล้ว
			System.out.println("=== createQuery , getResultStream ===");
			try (Stream<?> rst = hss.getSession().createQuery("from User").getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					System.out.println(e.getUserid() + " : " + e.getUsername());
				});
			}

			// test native query ผ่านแล้ว
			System.out.println("=== createNativeQuery , getResultStream ===");
			try (Stream<?> rst = hss.getSession().createNativeQuery("select * from user_table").getResultStream();) {
				rst.forEach(ebj -> {
					Object[] e = (Object[]) ebj;
					System.out.println(e[0] + " : " + e[1]);
				});
			}

			// test native query ผ่านแล้ว + ScrollMode.FORWARD_ONLY
			System.out.println("=== createNativeQuery , ScrollMode.FORWARD_ONLY ===");
			try (ScrollableResults rst = hss.getSession().createNativeQuery("select * from user_table")
					.scroll(ScrollMode.FORWARD_ONLY);) {
				while (rst.next()) {
					//System.out.println(rst.get(0) + " : " + rst.get(1));
					System.out.println(rst.get(1).toString());
				}
			}
			
			//อ่านเข้า tuple เหมาะกับ NativeQuery สำหรับออกรายงาน
			System.out.println("=== start read to tuple ===");
			try (Stream<Tuple> rst = hss.getSession().createNativeQuery("select * from user_table", Tuple.class).getResultStream();) {
				rst.forEach((record) -> {
					System.out.println(record.get("user_id", Integer.class) + "," + record.get("user_name", String.class));
				});
			} catch (Exception e2) {
				System.out.println("=== tuple error ===");
				e2.printStackTrace();
			}
			System.out.println("=== end read to tuple ===");

		}

	}

	public static void testUpdate() throws Exception {
		System.out.println("======testUpdate()");
		// ทดสอบปรับปรุงข้อมูล
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// แบบที่ 1 ค้นหาด้วย key // test ผ่านแล้ว
			User us1 = (User) hss.getSession().get(User.class, 304);
			us1.setUsername("user 304 x");
			hss.update(us1);

			// แบบที่ 1 // test HQL/JPQL + parameter ผ่านแล้ว
			Query<?> qr1 = hss.getSession().createQuery("from User where userid =:userid ");
			qr1.setParameter("userid", 305);
			try (Stream<?> rst = qr1.getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					e.setUsername("user 305x");
					hss.update(e);
				});
			}

			hss.commit();
		}

	}

	public static void testClone() throws Exception {
		System.out.println("======testClone()");
		// ทดสอบ clone
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// test HQL/JPQL ผ่านแล้ว
			Query<?> qr1 = hss.getSession().createQuery("from User where userid =:userid ");
			qr1.setParameter("userid", 305);
			try (Stream<?> rst = qr1.getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					e.setUserid(306);//เปลี่ยน key
					e.setUsername("clone from user 305x");
					hss.insert(e);
					
					e.setUserid(307);//เปลี่ยน key
					e.setUsername("clone from user 305x");
					hss.insert(e);
				});
			}

			hss.commit();
		}

	}

	public static void testDelete() throws Exception {
		System.out.println("======testDelete()");
		// ทดสอบ ลบ
		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			// แบบที่ 1 // test ผ่านแล้ว
			User us1 = (User) hss.getSession().get(User.class, 306);
			hss.delete(us1);
			
			// แบบที่ 2 //test HQL/JPQL ผ่านแล้ว
			Query<?> qr1 = hss.getSession().createQuery("from User where userid =:userid ");
			qr1.setParameter("userid", 307);
			try (Stream<?> rst = qr1.getResultStream();) {
				rst.forEach(ebj -> {
					User e = (User) ebj;
					hss.delete(e);
				});
			}

			hss.commit();
		}

	}

	public static void testdeleteALL() throws Exception {
		System.out.println("======testdeleteALL()");

		try (HStatelessSession hss = HStatelessSession.sessionDb();) {
			hss.begintrans();

			int eff = hss.getSession().createNativeQuery("delete from user_table").executeUpdate();
			System.out.println("eff : "+eff+" records");

			hss.commit();
		}

	}
	
}
