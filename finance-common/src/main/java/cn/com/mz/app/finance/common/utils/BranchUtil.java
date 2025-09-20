package cn.com.mz.app.finance.common.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <T>
 * @author mz
 *  用于实现函数式操作 if 语句
 */
public class BranchUtil<T> {
    private static final BranchUtil<?> EMPTY = new BranchUtil<>();
    private T value;
    private Supplier fastSupplier;

    private BranchUtil(T value) {
        this();
        this.value = value;
    }

    private BranchUtil() {

    }

    public static <T> BranchUtil<T> of(T value) {
        return new BranchUtil<>(value);
    }

    /**
     * 构建快速 if else 语义
     *
     * @param value
     * @return
     */
    public static BranchUtil<Boolean> ofBoolean(Boolean value) {
        return new BranchUtil<>(value);
    }

    public static <T> BranchUtil<T> of() {
        return new BranchUtil<>();
    }

    /**
     * @return
     */
    public Chain<T> chain() {
        Chain<T> chain = new Chain<>();
        chain.value = this.value;
        return chain;
    }

    /**
     * If else 模式
     * if(true){
     * return true;
     * }else{
     * return false;
     * }
     *
     * @return
     */
    public IfOrElse<T> whenIf() {
        return new IfOrElse<>(value);
    }


    /**
     * @param thenGet
     * @return
     */
    public BranchUtil<Boolean> ifTrue(Supplier thenGet) {
        if ((Boolean) this.value) {
            this.fastSupplier = thenGet;
        }
        return (BranchUtil<Boolean>) this;
    }

    public Object orElse(Supplier orElseSupplier) {
        if (!(Boolean) this.value) {
            return orElseSupplier.get();
        }
        return fastSupplier.get();
    }

    /**
     * if(true){
     * runnable.run();
     * }
     *
     * @param runnable
     */
    public void consumeTrue(Runnable runnable) {
        if ((Boolean) this.value) {
            runnable.run();
        }
    }


    /**
     * if(true){
     * throw new RuntimeException
     * }
     *
     * @param exceptionSupplier
     * @param <X>
     * @throws X
     */
    public <X extends Throwable> void thenThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if ((Boolean) this.value) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * if else 模式
     *
     * @param <T>
     */
    public static final class IfOrElse<T> {
        private Supplier<Boolean> condition;
        private boolean hadMatch;

        private T value;
        private Object result;

        public IfOrElse(T value) {
            this.value = value;
        }

        public IfOrElse<T> match(Predicate<T> predicate) {
            this.condition = () -> predicate.test(value);
            return this;
        }


        public IfOrElse<T> ifTrue(Supplier resultSupplier) {
            //只允许匹配一次
            if (!hadMatch && condition.get()) {
                this.result = resultSupplier.get();
                hadMatch = true;
            }
            return this;
        }

        public IfOrElse<T> ifTrue(Consumer<T> resultConsume) {
            if (!hadMatch && condition.get()) {
                resultConsume.accept(value);
                hadMatch = true;
            }
            return this;
        }

        public <X extends Throwable> IfOrElse<T> orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            if (result == null && !hadMatch) {
                throw exceptionSupplier.get();
            }
            return this;
        }

        public Object orElseGet(Supplier resultSupplier) {
            if (!hadMatch) {
                return resultSupplier.get();
            }
            return this.result;
        }

        public void orElse(Consumer<T> resultConsumer) {
            if (!hadMatch) {
                resultConsumer.accept(value);
            }
        }


    }

    /**
     * if if if 写法
     *
     * @param <T>
     */
    public final class Chain<T> {

        private Supplier<Boolean> condition;
        private T value;
        private boolean hadMatch;

        public Chain<T> match(Predicate<T> predicate) {
            this.condition = () -> predicate.test(value);
            return this;
        }

        public Chain<T> ifTrue(Supplier resultSupplier) {
            if (checkCondition()) {
                hadMatch = true;
                resultSupplier.get();
            }
            return this;
        }

        public Chain<T> ifTrue(Consumer<T> resultConsume) {
            if (checkCondition()) {
                resultConsume.accept(value);
            }
            return this;
        }


        public boolean checkCondition() {
            return condition.get();
        }

        public void orElse(Supplier resultSupplier) {
            if (!hadMatch) {
                resultSupplier.get();
            }
        }

        public void orElse(Consumer<T> resultConsumer) {
            if (!hadMatch) {
                resultConsumer.accept(value);
            }
        }

    }

}
