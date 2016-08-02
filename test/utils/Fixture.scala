package utils

trait Fixture {

  def fixture(): Unit

  def withFixture(test: => Unit): Unit = {
    try {
      test
    }
    finally {
      fixture()
    }
  }

}
