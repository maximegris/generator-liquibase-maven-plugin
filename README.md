# Liquibase Changelog Maven Plugin 
[![Build Status](https://travis-ci.org/maximegris/generator-liquibase-maven-plugin.svg?branch=master)](https://travis-ci.org/maximegris/generator-liquibase-maven-plugin) 
[![License](https://img.shields.io/badge/license-Apache2-blue.svg?style=flat)](https://github.com/mjiderhamn/classloader-leak-prevention/blob/master/LICENSE.md)


This is a Maven plugin for [Liquibase Changelog Lib](https://github.com/liquibase/liquibase).

## Usage ##

Here is and example that will generate a db.changelog-master-postgresql.xml. 

```
  <build>
		<plugins>
			<plugin>
 				<groupId>com.github.maximegris</groupId>
				<artifactId>generator-liquibase-maven-plugin</artifactId>
				<version>1.0.0</version>
				<executions>
					<execution>
						<phase>generate-resources</phase>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
  </build>
```

Configuration options (all are optionals)

| Alias  | Values  | Default  |
|---|---|---|
| liquibaseVersion | Liquibase version to target (XSD headers in XML) | 3.1 |
| filesLocation | Directory of changelogs files. Must be defined with ${basedir} when using Maven module pattern  | src/main/resources |
| sqlChangelogFormat | Type of master changelog to generate  - oracle, postgresql,... (example db.changelog-master-postgresql.xml) | postgresql |
| filePattern | Pattern of file to add in master changelog. Get all files in resources directory if not defined | * |
| filePatternCustomSort | Apply a custom sort pattern on some files. During comparison, if one or both files don't match the pattern, it use String.equals | Empty string |
| customFilesToIgnore | List of file to ignore/remove of master changelog. Use ; to separate them | Empty string |
| customFilesToInsert | List of file which doesn't match the pattern and to insert in master changelog. Use ; to separate them | Empty string |

## Example with configuration options ##

This will generate a db.changelog-master-postgresql.xml.
It will also ignore some files and insert other files that don't respect the defined files pattern.

```
  <build>
		<plugins>
			<plugin>
 				<groupId>com.github.maximegris</groupId>
				<artifactId>generator-liquibase-maven-plugin</artifactId>
				<version>1.0.0</version>
				<executions>
					<execution>
						<phase>generate-resources</phase>
						<goals>
							<goal>generate</goal>
						</goals>
						<configuration>
							<filesLocation>${basedir}/src/main/resources/db/changelogs</filesLocation>
							<filePattern>^db.changelog-[1-9]+.*-postgresql\.xml$</filePattern>
							<filePatternCustomSort>[1-9]+\.[0-9]+</filePatternCustomSort>
							<customFilesToIgnore>db.changelog-2.1-postgresql.xml;db.changelog-2.21-postgresql.xml</customFilesToIgnore>
							<customFilesToInsert>db.changelog-2.5-postgresql.sql;db.changelog-2.4-postgresql.sql;db.changelog-2.3-postgresql.sql</customFilesToInsert>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
  </build>
```

## Developpers ##

To generate master changelog, just run in your project:
```
mvn generate-resources
```
Or
```
mvn clean install
```
