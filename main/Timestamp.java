package main;

public class Timestamp {
  public Timestamp () {
    this (0, 0);
  }

  public Timestamp (int read, int write) {
    this.read = read;
    this.write = write;
  }

  public int read, write;
}
