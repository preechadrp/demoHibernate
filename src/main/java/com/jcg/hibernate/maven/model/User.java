package com.jcg.hibernate.maven.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder(toBuilder=true) //เพื่อให้สามารถสร้าง object ใหม่ได้จาก object เดิมด้วยคำสั่ง objOld.toBuilder().build(); เป็นการ clone มาเป็นตัวใหม่
@Accessors(chain=true)  //ทำให้ใช้คำสั่ง User mm = new User().setUserid(1).setUsername("kk"); ได้
@AllArgsConstructor //สร้าง method Constructor แบบมี Argument ทุก member
@NoArgsConstructor  //สร้าง method Constructor แบบไม่มี Argument
@Data
@Entity
@Table(name = "user_table")
public class User  {

	@Id
	@Column(name = "user_id")
	private int userid;

	@Column(name = "user_name", length = 255)
	@Length(min = 1, max = 255, message = "ความยาวของชื่อสุดท้ายต้องอยู่ระหว่าง 1 ถึง 255 ตัวอักษร")
	private String username;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private java.time.LocalDateTime createdDate;
	
}