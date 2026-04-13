// Examples based on https://avro.apache.org/docs/1.11.1/getting-started-java/#download
package de.thi.informatik.edi;

import java.io.File;
import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import de.thi.informatik.edi.dto.User;

public class Example {
	private static final File EXAMPLE_FILE = new File("users.avro");

	public static void main(String[] args) {
		// Creating objects based on avro schema
		User user1 = new User();
		user1.setName("Alyssa");
		user1.setFavoriteNumber(256);

		User user2 = new User("Ben", 7, "red");

		User user3 = User.newBuilder()
		             .setName("Charlie")
		             .setFavoriteColor("blue")
		             .setFavoriteNumber(null)
		             .build();
		
		// Serialize user1, user2 and user3 to disk
		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
		try(DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter)) {
			dataFileWriter.create(user1.getSchema(), EXAMPLE_FILE);
			dataFileWriter.append(user1);
			dataFileWriter.append(user2);
			dataFileWriter.append(user3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Deserialize Users from disk
		DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
		try(DataFileReader<User> dataFileReader = new DataFileReader<User>(EXAMPLE_FILE, userDatumReader)) {			
			User user = null;
			while (dataFileReader.hasNext()) {
				user = dataFileReader.next(user);
				System.out.println(user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}