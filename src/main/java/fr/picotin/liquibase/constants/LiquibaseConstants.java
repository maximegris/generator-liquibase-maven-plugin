package fr.picotin.liquibase.constants;

public class LiquibaseConstants {

	public static final String XML_START = new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
			.append("<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
			.append("  xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-${liquibaseVersion}.xsd\" ")
			.append("  logicalFilePath=\"changelog-master-${sqlType}\">\n")
			.append("<property name=\"blob\" value=\"OID\" dbms=\"${sqlType}\"/>\n").toString();

	public static final String XML_END = new StringBuilder().append("</databaseChangeLog>").toString();

}
