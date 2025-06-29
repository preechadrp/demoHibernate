/*
 * ทดสอบการใช้ Session ปกติ
 */
package com.jcg.hibernate.maven;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.jcg.hibernate.maven.model.User;

public class HSession implements AutoCloseable {

	// ตั้งชื่อตามฐานที่ต้องการเชื่อม 1 SessionFactory ต่อ 1 Database
	private static SessionFactory sessionFactoryObjMasterDb;
	// private static SessionFactory sessionFactoryObjPicDb;
	// //กรณีต้องเชื่อมฐานภาพหรือฐานอื่นๆ (เชื่อมมากกว่า 1 ฐาน) เป็นต้น

	private Session sessionObj;
	// private StatelessSession sessionObj;
	//private boolean useTransaction = false;
	private Transaction tx;

	/**
	 * เชื่อมฐานข้อมูลตัวหลัก
	 * 
	 * @return
	 */
	public static HSession sessionDb() {
		HSession hsession1 = new HSession();
		hsession1.setSessionDb();
		return hsession1;
	}

	private void setSessionDb() {

		// ==== Since Hibernate Version 4.x, ServiceRegistry Is Being Used
		// Configuration configObj = new Configuration();
		// configObj.addAnnotatedClass(User.class);//ลองแบบนี้ผ่านมองเห็นการ mapping
		// แต่ใส่ hibernate.cfg.xml ไม่ผ่าน
		// configObj.configure("hibernate.cfg.xml");

		// ServiceRegistry serviceRegistryObj = new
		// StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

		// ==== Creating Hibernate SessionFactory Instance
		// sessionFactoryObjMasterDb =
		// configObj.buildSessionFactory(serviceRegistryObj);
		// sessionObj = sessionFactoryObjMasterDb.openSession();

		// === test on Hibernate Version 5.6.x
		if (sessionFactoryObjMasterDb == null) {

			// ระบบจะเชื่อมฐานจังหวะสร้าง sessionFactoryObjMasterDb
			// ถ้ามีการกำหนดการเชื่อมฐานข้อมูลไว้

			// ServiceRegistry serviceRegistryObj = new
			// StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
			// แบบที่ 1 //test ผ่าน
			// sessionFactoryObjMasterDb = new
			// MetadataSources(serviceRegistryObj).buildMetadata().buildSessionFactory();
			// แบบที่ 2 //test ผ่าน
			// sessionFactoryObjMasterDb = new
			// Configuration().buildSessionFactory(serviceRegistryObj);

			// แบบที่ 3
			sessionFactoryObjMasterDb = new Configuration().configure("hibernate.cfg.xml")
					// can also use this line to configure all models
					// .addPackage("com.jcg.hibernate.maven.model") //ไม่ผ่าน
					// .addAnnotatedClass(User.class) //test ผ่าน
					.buildSessionFactory();

			System.out.println("create.. sessionFactoryObjMasterDb");
		}
		sessionObj = sessionFactoryObjMasterDb.openSession();

	}

	public HSession() {
		// Locale.setDefault(Locale.ENGLISH);//สำคัญมาก ช่วยแก้ปัญหาเรื่อง
		// Preparestatment (setDate, setTimestamp)
		if (Locale.getDefault() != Locale.ENGLISH) {
			Locale.setDefault(Locale.ENGLISH);
		}
	}

	/**
	 * เริ่มทำ transaction
	 */
	public void begintrans() {
		tx = sessionObj.beginTransaction();
	}

	/**
	 * commit transaction
	 */
	public void commit() {
		if (tx != null) {
			if (tx.isActive()) {
				tx.commit();
				System.out.println("commit");
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (sessionObj != null) {
			if (tx != null ) {
				if (tx.isActive()) {
					tx.rollback();
					System.out.println("rollback");
				}
			}
			sessionObj.close();
			System.out.println("close");
		}
	}

	/**
	 * save ตารางลงฐานจริง
	 * 
	 * @param entity
	 */
	public void save(Object entity) {
		sessionObj.save(entity);
	}

	/**
	 * persist ตารางลงฐานจริง
	 * 
	 * @param entity
	 */
	public void persist(Object entity) {
		sessionObj.persist(entity);
	}

	/**
	 * ดึง session ปัจจุบัน
	 * 
	 * @return session
	 */
	public Session getSession() {
		return sessionObj;
	}

}
