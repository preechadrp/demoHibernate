/*
 * ทดสอบการใช้ StatelessSession
 */
package com.jcg.hibernate.maven;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class HStatelessSession implements AutoCloseable {

	private static SessionFactory sessionFactoryObjMasterDb;
	//กรณีเชื่อมฐานข้อมูลอื่น แยกคนละ SessionFactory
	//private static SessionFactory sessionFactoryObjMasterDbPic;  
	//private static SessionFactory sessionFactoryObj......; ฯลฯ  
	
	private StatelessSession sessionObj;
	private Transaction tx;
	
	/**
	 * เชื่อมฐานข้อมูลตัวหลัก
	 * @return
	 */
	public static HStatelessSession sessionDb() {
		HStatelessSession hStSession1 = new HStatelessSession();
		hStSession1.setSessionDb();
		return hStSession1;
	}

	private void setSessionDb() {
				
		// === test on Hibernate Version 5.6.x
		if (sessionFactoryObjMasterDb == null) {
			
			System.out.println("create.. sessionFactoryObjMasterDb");
			
			//ระบบจะเชื่อมฐานจังหวะสร้าง sessionFactoryObjMasterDb ถ้ามีการกำหนดการเชื่อมฐานข้อมูลไว้
			System.out.println("load config");
			Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
			
			//cfg.addPackage("com.jcg.hibernate.maven.model")  //ไม่ผ่าน
			
			//cfg.addAnnotatedClass(com.jcg.hibernate.maven.model.User.class)  //test ผ่าน

			//เพิ่มจาก String  //test  ผ่าน 17/5/68
			Class<?> cls = getClassFromString("com.jcg.hibernate.maven.model.User");
			if (cls != null) {
				cfg.addAnnotatedClass(cls);
			}
			
			//System.out.println(cfg.getProperties().getProperty("hibernate.connection.url"));//test ผ่าน
			System.out.println("buildSessionFactory()");
			sessionFactoryObjMasterDb = cfg.buildSessionFactory();
			
			//==for hibernate 5.x
			//StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
			//sessionFactoryObjMasterDb = new MetadataSources(registry).buildMetadata().buildSessionFactory();

		}
		sessionObj = sessionFactoryObjMasterDb.openStatelessSession();
		
//		=== ยังทดสอบไม่ผ่าน 30/6/66
//		if (sessionFactoryObjMasterDb == null) {
//			System.out.println("create.. sessionFactoryObjMasterDb");
//			//ระบบจะเชื่อมฐานจังหวะสร้าง sessionFactoryObjMasterDb ถ้ามีการกำหนดการเชื่อมฐานข้อมูลไว้
//			sessionFactoryObjMasterDb = new Configuration().configure("hibernate2.cfg.xml").buildSessionFactory();
//		}
//		sessionObj = sessionFactoryObjMasterDb.openStatelessSession(DBHelper.getConnection());

	}
	
	/**
	 * ตัวอย่างสร้าง config แบบไม่มี xml
	 * @throws ClassNotFoundException 
	 */
	public void setSessionDbWithoutXmlConfig() throws ClassNotFoundException {

		if (sessionFactoryObjMasterDb == null) {
			System.out.println("create.. sessionFactoryObjMasterDb");
			
			Configuration hCfg = new org.hibernate.cfg.Configuration();
			
			Properties prop = new Properties();
			prop.setProperty("hibernate.connection.url", "jdbc:mysql://192.168.0.101:3306/go");
			prop.setProperty("hibernate.connection.username", "go");
			prop.setProperty("hibernate.connection.password", "go");//สามารถอ่านแล้ว update ค่ากลับได้ เอาไปพริกแพลงการเข้ารหัสผ่านถอดรหัสผ่านได้
			prop.setProperty("dialect", "org.hibernate.dialect.MySQLDialect");
			prop.setProperty("hibernate.hbm2ddl.auto", "update");//update,create
			//... 
			hCfg.addProperties(prop);
			
			 //ยังไม่เข้าใจตัวนี้
			//hCfg.addPackage("com.kat")
			
			//แบบอ่านเข้าที่ละตัว
			//hCfg.addAnnotatedClass(User.class); 
			//หรือ
			//hCfg.addAnnotatedClass(Class.forName("com.jcg.hibernate.maven.model.User"));
			//ซึ่งค่า "com.jcg.hibernate.maven.model.User" เราประยุกต์เก็บใน text file ได้
			
			//แบบ scan ทั้ง package ต้องเพิ่ม reflections ในไฟล์ pom.xml
			Set<Class> lstClassEntities = findAllClassesUsingReflectionsLibrary("com.jcg.hibernate.maven.model");
			lstClassEntities.forEach((cls) -> {
				hCfg.addAnnotatedClass(cls);					
			});
						
			sessionFactoryObjMasterDb = hCfg.buildSessionFactory();
		}
		sessionObj = sessionFactoryObjMasterDb.openStatelessSession();

	}
	
	public Set<Class> findAllClassesUsingReflectionsLibrary(String packageName) {
	    Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
	    return reflections.getSubTypesOf(Object.class)
	      .stream()
	      .collect(Collectors.toSet());
	}
	
	/**
	 * สร้่าง class จาก String
	 * @param cls  ตำแหน่ง class ใน .jar  เช่น package1.MyClass เป็นต้น
	 * @return
	 */
	public Class<?> getClassFromString(String cls) {
		try {
			Class<?> clazz = Class.forName(cls);
			Object instance = clazz.getDeclaredConstructor().newInstance();
			return instance.getClass();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HStatelessSession() {
		// Locale.setDefault(Locale.ENGLISH);//สำคัญมาก ช่วยแก้ปัญหาเรื่อง
		// Preparestatment (setDate, setTimestamp)
		// แต่ไม่มีผลถ้าใช้ hibernate ที่ฟิลด์วันที่ใช้ java.time.LocalDate และ java.time.OffsetDateTime  (test 28/02/2026)
		if (Locale.getDefault() != Locale.ENGLISH) {
			Locale.setDefault(Locale.ENGLISH);
		}
	}

	/**
	 * เริ่มทำ transaction
	 */
	public void begintrans() {
		System.out.println("beginTransaction");
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
			System.out.println("close session");
		}
	}
	
	/**
	 * insert ตารางลงฐานจริง
	 * 
	 * @param entity
	 */
	public void insert(Object entity) {
		sessionObj.insert(entity);
	}

	/**
	 * update ตารางลงฐานจริง
	 * 
	 * @param entity
	 */
	public void update(Object entity) {
		sessionObj.update(entity);
	}
	
	/**
	 * delete ตารางลงฐานจริง
	 * 
	 * @param entity
	 */
	public void delete(Object entity) {
		sessionObj.delete(entity);
	}

	/**
	 * ดึง session ปัจจุบัน
	 * 
	 * @return session
	 */
	public StatelessSession getSession() {
		return sessionObj;
	}
	
}
