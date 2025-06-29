package test;
import org.junit.Test;

import com.jcg.hibernate.maven.TestHSession;

public class ApTestHSession {

	@Test
	public void test1() {
		TestHSession.test1(); //test ผ่าน
	}
	
	@Test
	public void testInsert() throws Exception {
		TestHSession.testInsert(); //test ผ่าน
	}
	
	@Test
	public void testRead() throws Exception {
		TestHSession.testRead(); //test ผ่าน
	}
	
	@Test
	public void testUpdate() throws Exception {
		TestHSession.testUpdate(); //test ผ่าน
	}
	
}
