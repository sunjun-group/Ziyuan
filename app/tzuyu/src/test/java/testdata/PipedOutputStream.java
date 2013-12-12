package testdata;

/**
 * This example is adapted from SAS 2012 paper "Symbolic Learning of Component
 * Interfaces" By Dimitra Giannakopoulou, etc.
 * 
 * @author Spencer Xiao
 * 
 */
public class PipedOutputStream {
  public PipedInputStream sink;

  public PipedOutputStream() {
    sink = null;
  }

  public void connect(PipedInputStream snk) {
    if (snk == null) {
      throw new IllegalArgumentException("Invalid parameter");
    } else if (sink != null || snk.connected) {
      throw new IllegalArgumentException("Invalid parameter");
    }

    sink = snk;
    snk.connected = true;
  }

  public void write() {
    if (sink == null) {
      throw new IllegalArgumentException("Invalid parameter");
    }
  }

  public void flush() {
    if (sink != null) {
    }
  }

  public void close() {
    if (sink != null) {
    }
  }
}
