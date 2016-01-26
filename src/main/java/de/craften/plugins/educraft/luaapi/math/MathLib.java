package de.craften.plugins.educraft.luaapi.math;

import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Random;

/**
 * The standard Lua math lib. Based on the implementation of LuaJ, but ported so that it doesn't require packages.
 *
 * @see <a href="http://www.lua.org/manual/5.1/manual.html#5.6">Lua 5.1 Reference Manual, section 5.6</a>
 */
public class MathLib extends LuaTable {
    public MathLib() {
        set("abs", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.abs(d);
            }
        });

        set("acos", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.acos(d);
            }
        });

        set("asin", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.asin(d);
            }
        });

        set("atan", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.atan(d);
            }
        });

        set("atan2", new BinaryOp() {
            @Override
            public double call(double x, double y) {
                return Math.atan2(x, y);
            }
        });

        set("ceil", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.ceil(d);
            }
        });

        set("cos", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.cos(d);
            }
        });

        set("cosh", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.cosh(d);
            }
        });

        set("deg", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.toDegrees(d);
            }
        });

        set("exp", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.exp(d);
            }
        });

        set("floor", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.floor(d);
            }
        });

        set("fmod", new BinaryOp() {
            @Override
            public double call(double x, double y) {
                double q = x / y;
                return x - y * (q >= 0 ? Math.floor(q) : Math.ceil(q));
            }
        });

        set("frexp", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double x = args.checkdouble(1);
                if (x == 0) return varargsOf(ZERO, ZERO);
                long bits = Double.doubleToLongBits(x);
                double m = ((bits & (~(-1L << 52))) + (1L << 52)) * ((bits >= 0) ? (.5 / (1L << 52)) : (-.5 / (1L << 52)));
                double e = (((int) (bits >> 52)) & 0x7ff) - 1022;
                return varargsOf(valueOf(m), valueOf(e));
            }
        });

        set("huge", LuaDouble.POSINF);

        set("ldexp", new BinaryOp() {
            @Override
            public double call(double x, double y) {
                return x * Double.longBitsToDouble((((long) y) + 1023) << 52);
            }
        });

        set("log", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.log(d);
            }
        });

        set("log10", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.log10(d);
            }
        });

        set("max", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double max = args.checkdouble(1);
                for (int i = 2, n = args.narg(); i <= n; i++) {
                    max = Math.max(max, args.checkdouble(i));
                }
                return valueOf(max);
            }
        });

        set("min", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double min = args.checkdouble(1);
                for (int i = 2, n = args.narg(); i <= n; i++) {
                    min = Math.min(min, args.checkdouble(i));
                }
                return valueOf(min);
            }
        });

        set("modf", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double x = args.checkdouble(1);
                double intPart = (x > 0) ? Math.floor(x) : Math.ceil(x);
                double fracPart = x - intPart;
                return varargsOf(valueOf(intPart), valueOf(fracPart));
            }
        });

        set("pi", Math.PI);

        set("pow", new BinaryOp() {
            @Override
            public double call(double x, double y) {
                return Math.pow(x, y);
            }
        });

        set("rad", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.toRadians(d);
            }
        });

        set("random", new RandomFunction());

        set("randomseed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                ((RandomFunction) MathLib.this.get("random")).random = new Random(arg.checklong());
                return NONE;
            }
        });

        set("sin", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.sin(d);
            }
        });

        set("sinh", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.sinh(d);
            }
        });

        set("sqrt", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.sqrt(d);
            }
        });

        set("tan", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.tan(d);
            }
        });

        set("tanh", new UnaryOp() {
            @Override
            public double call(double d) {
                return Math.tanh(d);
            }
        });
    }

    private static abstract class UnaryOp extends OneArgFunction {
        public LuaValue call(LuaValue arg) {
            return valueOf(call(arg.checkdouble()));
        }

        public abstract double call(double d);
    }

    private static abstract class BinaryOp extends TwoArgFunction {
        public LuaValue call(LuaValue x, LuaValue y) {
            return valueOf(call(x.checkdouble(), y.checkdouble()));
        }

        public abstract double call(double x, double y);
    }

    private static class RandomFunction extends VarArgFunction {
        Random random = new Random();

        @Override
        public Varargs invoke(Varargs args) {
            if (args.narg() == 0) {
                return valueOf(random.nextDouble());
            } else if (args.narg() == 1) {
                int m = args.checkint(1);
                if (m < 1) argerror(1, "interval is empty");
                return valueOf(1 + random.nextInt(m));
            } else {
                int m = args.checkint(1);
                int n = args.checkint(2);
                if (n < m) argerror(2, "interval is empty");
                return valueOf(m + random.nextInt(n + 1 - m));
            }
        }
    }
}
