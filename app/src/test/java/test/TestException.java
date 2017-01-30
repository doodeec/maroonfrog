package test;

/**
 * Helper Exception for unit testing
 * provides a way to compare two exceptions to test if they are equal
 *
 * @author Dusan Bartos
 */
public final class TestException extends Throwable {

    public TestException(String detailMessage) {
        super(detailMessage);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestException that = (TestException) o;
        return !(getMessage() != null ? !getMessage().equals(that.getMessage()) : that.getMessage() != null);
    }

    @Override public String toString() {
        return "Exception: " + getMessage();
    }
}
