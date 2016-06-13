package java.lang;

/**
 * <code>Runnable</code> should be implemented by any
 * class whose instances are intended to be executed by a thread. The
 * class must define a method of no arguments called <code>run</code>.
 *###########
 *  实现Runnable的类的实例必须使用一个线程进行执行,这个实现类必须定义(覆写)一个无参方法run
 *###########
 *
 *
 *
 * <p>
 * This interface is designed to provide a common protocol for objects that
 * wish to execute code while they are active. For example,
 * <code>Runnable</code> is implemented by class <code>Thread</code>.
 * Being active simply means that a thread has been started and has not
 * yet been stopped.
 * <p>
 *#################
 *  设计该接口的目的是为希望在激活时执行代码的对象提供一个公共协议。例如，Thread 类实现了 Runnable。
 *  激活的意思是说某个线程已启动并且尚未停止。
 *#############
 *
 *
 *
 *
 * In addition, <code>Runnable</code> provides the means for a class to be
 * active while not subclassing <code>Thread</code>.
 *##########
 * 此外，Runnable 为非 Thread 子类的类提供了一种激活方式。
 *##########
 *
 * A class that implements
 * <code>Runnable</code> can run without subclassing <code>Thread</code>
 * by instantiating a <code>Thread</code> instance and passing itself in
 * as the target.
 * ############
 * 通过实例化某个 Thread 实例并将自身作为运行目标，就可以运行实现 Runnable 的类而无需创建 Thread 的子类。
 * ############
 *
 *
 * In most cases, the <code>Runnable</code> interface should
 * be used if you are only planning to override the <code>run()</code>
 * method and no other <code>Thread</code> methods.
 * This is important because classes should not be subclassed
 * unless the programmer intends on modifying or enhancing the fundamental
 * behavior of the class.
 *###########
 * 大多数情况下，如果只想重写 run() 方法，而不重写其他 Thread 方法，那么应使用 Runnable 接口。
 * 这很重要，因为除非程序员打算修改或增强类的基本行为，否则不应为该类创建子类。
 *###############
 *
 *
 * @author  Arthur van Hoff
 * @see     java.lang.Thread
 * @see     java.util.concurrent.Callable
 * @since   JDK1.0
 */
public
interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     *##############
     * 使用实现接口 Runnable 的对象创建一个线程时，启动该线程将导致在独立执行的线程中调用对象的 run 方法。
     *##############
     *
     *
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *##############
     *方法 run 的常规协定是，它可能执行任何所需的动作。
     *###########
     *
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}
