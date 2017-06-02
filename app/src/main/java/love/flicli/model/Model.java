package love.flicli.model;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;

import love.flicli.MVC;

/**
 * Created by tommaso on 18/05/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;
    public static final String API = "7d915cac1d6d251a1014bc8e00a9bf2e";
    // I really need that info?
    public static final String SECRET_KEY = "10fde0314d4d1aef";

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

}
