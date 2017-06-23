package tzuyu.testdata;

import java.security.InvalidKeyException;
import java.security.SignatureException;

public class Signature {
  private static final int UNINITIALIZED = 0;
  private static final int SIGN = 1;
  private static final int VERIFY = 2;

  private int state;

  public Signature() {
    state = UNINITIALIZED;
  }

  public final void initVerify() throws InvalidKeyException {
    state = VERIFY;
  }

  public final void initSign() throws InvalidKeyException {
    state = SIGN;
  }

  public final void sign() throws SignatureException {
    if (state == SIGN) {
    } else {
      throw new SignatureException("object not initialized for " + "signing");
    }
  }

  public final boolean verify() throws SignatureException {
    if (state == VERIFY) {
      return true;
    }
    throw new SignatureException("object not initialized for " + "verification");
  }

  public final void update() throws SignatureException {
    if (state == VERIFY || state == SIGN) {
    } else {
      throw new SignatureException("object not initialized for "
          + "signature or verification");
    }
  }
  
  public int getState() {
  	return state;
  }
}
