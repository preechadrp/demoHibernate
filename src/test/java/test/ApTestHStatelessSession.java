package test;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jcg.hibernate.maven.TestHStatelessSession;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApTestHStatelessSession {
		
	@Test
	public void F00testInsert() throws Exception {
		TestHStatelessSession.testdeleteALL(); //test ผ่าน 29/6/66
	}
	
	@Test
	public void F01testInsert() throws Exception {
		TestHStatelessSession.testInsert(); //test ผ่าน 29/6/66
	}
	
//	@Test
//	public void F02testRead() throws Exception {
//		TestHStatelessSession.testRead(); //test ผ่าน 29/6/66
//	}
//	
//	@Test
//	public void F03testUpdate() throws Exception {
//		TestHStatelessSession.testUpdate();//test ผ่าน 29/6/66 
//	}
//	
//	@Test
//	public void F04testClone() throws Exception {
//		TestHStatelessSession.testClone();//test ผ่าน 29/6/66 
//	}
//	
//	@Test
//	public void F05testDelete() throws Exception {
//		TestHStatelessSession.testDelete();//test ผ่าน 29/6/66 
//	}

}
