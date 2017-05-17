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

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

}
