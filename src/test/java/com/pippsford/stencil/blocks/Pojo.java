package com.pippsford.stencil.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple POJO bean for testing.
 *
 * @author Simon Greatrix on 06/01/2021.
 */
public class Pojo {

  private int age;

  private String name;

  private Pojo other;

  private Map<String, Object> properties = new HashMap<>();

  private List<String> qualifications;

  private String title;


  /**
   * New instance.
   *
   * @param title the person's title
   * @param name  the person's name
   * @param age   the person's age
   */
  public Pojo(String title, String name, int age) {
    this.name = name;
    this.age = age;
    this.title = title;
  }


  public int getAge() {
    return age;
  }


  public String getError() {
    throw new NegativeArraySizeException();
  }


  public String getName() {
    return name;
  }


  public Pojo getOther() {
    return other;
  }


  public Map<String, Object> getProperties() {
    return properties;
  }


  public List<String> getQualifications() {
    return qualifications;
  }


  public String getTitle() {
    return title;
  }


  public void setAge(int age) {
    this.age = age;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setOther(Pojo other) {
    this.other = other;
  }


  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }


  public void setQualifications(List<String> qualifications) {
    this.qualifications = qualifications;
  }


  public void setTitle(String title) {
    this.title = title;
  }

}
