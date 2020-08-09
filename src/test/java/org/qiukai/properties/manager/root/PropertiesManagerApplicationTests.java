package org.qiukai.properties.manager.root;

import org.junit.jupiter.api.Test;
import org.qiukai.properties.manager.util.PropertiesReadWriteUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.util.HashMap;

@SpringBootTest
class PropertiesManagerApplicationTests {

	@Test
	public void save(){

		HashMap<String, String> data = new HashMap<>();
		data.put("a","1");
		data.put("b","2");
		data.put("c","3");

		//
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("D:/data.jdl"))){

			output.writeObject(data);
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void read(){

		HashMap data = PropertiesReadWriteUtil.read2Jdl("D:/data.jdl", HashMap.class);
		System.out.println(data);
	}

	@Test
	public void files() throws Exception{

		ClassPathResource data = new ClassPathResource("jdl/data.jdl");
		FileSystemResource resource = new FileSystemResource("src/main/resources/jdl/data.jdl");
		System.out.println(resource.getFile().getAbsolutePath());

		HashMap<String, String> d = new HashMap<>();
		d.put("a","1");
		PropertiesReadWriteUtil.save2Jdl(resource.getFile().getAbsolutePath(), d);
	}
}
