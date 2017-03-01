/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;

/**
 * {@code Lock} implementations provide more extensive locking
 * operations than can be obtained using {@code synchronized} methods
 * and statements.  They allow more flexible structuring, may have
 * quite different properties, and may support multiple associated
 * {@link Condition} objects.
 *
 * Lock的实现类提供扩展性更强的锁操作,可以获得使用synchronized的方法和代码块同样的效果
 * 它们允许更灵活的结构,可能有非常不同的属性,和可能支持多联系的Condition对象
 *
 * <p>A lock is a tool for controlling access to a shared resource by
 * multiple threads. Commonly, a lock provides exclusive access to a
 * shared resource: only one thread at a time can acquire the lock and
 * all access to the shared resource requires that the lock be
 * acquired first. However, some locks may allow concurrent access to
 * a shared resource, such as the read lock of a {@link ReadWriteLock}.
 * 锁是一个用来控制多线程访问共享资源的工具,通常,一个锁提供单独访问共享资源的效果,
 * 同一只有一个线程可以获得锁且需要是第一个,无论如何,一些锁可能允许并发访问某个共享资源
 * 比如像在ReadWriteLock里面的读锁
 *
 *
 * <p>The use of {@code synchronized} methods or statements provides
 * access to the implicit monitor lock associated with every object, but
 * forces all lock acquisition and release to occur in a block-structured way:
 * when multiple locks are acquired they must be released in the opposite
 * order, and all locks must be released in the same lexical scope in which
 * they were acquired.
 *
 * 使用synchronized的方法伙代码块提供通过隐式的monitor锁指定任意的对象来实现,
 * 但所有的锁获得和释放必须都在阻塞块的方法内发生,当需要多个锁它们必须按照相反的顺序释放,并且所有锁释放在它们需要的同一词法范围
 *
 * <p>While the scoping mechanism for {@code synchronized} methods
 * and statements makes it much easier to program with monitor locks,
 * and helps avoid many common programming errors involving locks,
 * there are occasions where you need to work with locks in a more
 * flexible way.
 * For example, some algorithms for traversing
 * concurrently accessed data structures require the use of
 * &quot;hand-over-hand&quot; or &quot;chain locking&quot;: you
 * acquire the lock of node A, then node B, then release A and acquire
 * C, then release B and acquire D and so on.
 * Implementations of the
 * {@code Lock} interface enable the use of such techniques by
 * allowing a lock to be acquired and released in different scopes,
 * and allowing multiple locks to be acquired and released in any
 * order.
 *
 * 但synchronized定义的方法和代码块可以简单的使用monitor锁来实现编码,也能
 * 帮助避免很多普遍的使用锁时的编程错误
 * 但有些场景需要跟灵活的方式去使用锁,
 * 比如一些算法并发访问某些数据结构需要交互的链式的锁你需要节点a的锁,节点b的锁,然后释放A获得C,释放B获得D,如此下去,
 * 实现Lock接口可以使用这些技巧语序多个锁在任何顺序下的获得和释放
 *
 * <p>With this increased flexibility comes additional
 * responsibility. The absence of block-structured locking removes the
 * automatic release of locks that occurs with {@code synchronized}
 * methods and statements. In most cases, the following idiom
 * should be used:
 *基于额外增加的灵活性特性,阻塞结构锁没有移除发生在synchronized声明的方法的代码块的锁的原子释放
 * 在更多的场合下,下面写法可以使用
 *
 * <pre><tt>     Lock l = ...;
 *     l.lock();
 *     try {
 *         // access the resource protected by this lock
 *     } finally {
 *         l.unlock();
 *     }
 * </tt></pre>
 *
 * When locking and unlocking occur in different scopes, care must be
 * taken to ensure that all code that is executed while the lock is
 * held is protected by try-finally or try-catch to ensure that the
 * lock is released when necessary.
 * 但锁和解锁发生在不同的区域,注意必须保证在锁使用try-finallly或者try-catch去保护的情况下代码执行去确保锁必须被释放
 *
 * <p>{@code Lock} implementations provide additional functionality
 * over the use of {@code synchronized} methods and statements by
 * providing a non-blocking attempt to acquire a lock ({@link
 * #tryLock()}), an attempt to acquire the lock that can be
 * interrupted ({@link #lockInterruptibly}, and an attempt to acquire
 * the lock that can timeout ({@link #tryLock(long, TimeUnit)}).
 *Lock的实现提供了额外的功能在synchronized方法和代码块在尝试获得一个锁的情况,
 * 可以尝试去获得这个锁的操作可以被终止(lockInterruptibly),也可以尝试获得锁的操作可以超时(tryLock)
 *
 * <p>A {@code Lock} class can also provide behavior and semantics
 * that is quite different from that of the implicit monitor lock,
 * such as guaranteed ordering, non-reentrant usage, or deadlock
 * detection. If an implementation provides such specialized semantics
 * then the implementation must document those semantics.
 *
 * Lock还可以提供行为和语义,这是完全不同于隐式监视器锁,
 * 如保证顺序,不可重入使用,或死锁检测。
 * 如果提供了一个实现这种专门的语义然后必须实现文档的语义。
 *
 * <p>Note that {@code Lock} instances are just normal objects and can
 * themselves be used as the target in a {@code synchronized} statement.
 * Acquiring the
 * monitor lock of a {@code Lock} instance has no specified relationship
 * with invoking any of the {@link #lock} methods of that instance.
 * It is recommended that to avoid confusion you never use {@code Lock}
 * instances in this way, except within their own implementation.
 *
 * 注意Lock实例只能是普通对象和自己就像synchronized代码块里面的目标对象一样
 * 获取监视器锁的Lock对象不能特殊指定实例的方法加锁
 * 建议避免混淆,除了自己的实现,不要使用以这种方式使用Lock实例,
 *
 *
 *
 *<p>Except where noted, passing a {@code null} value for any
 * parameter will result in a {@link NullPointerException} being
 * thrown.
 *
 * <h3>Memory Synchronization</h3>
 *
 * <p>All {@code Lock} implementations <em>must</em> enforce the same
 * memory synchronization semantics as provided by the built-in monitor
 * lock, as described in section 17.4 of
 * <cite>The Java&trade; Language Specification</cite>:
 * 所有Lock必须实现相同的内存同步语义,提供就像内置的监控锁一样,如17.4节所述(Java语言规范):
 *
 * <ul>
 * <li>A successful {@code lock} operation has the same memory
 * synchronization effects as a successful <em>Lock</em> action.
 * <li>A successful {@code unlock} operation has the same
 * memory synchronization effects as a successful <em>Unlock</em> action.
 * </ul>
 *
 * 作为成功的Lock,相同的加锁解锁锁操作必须相同的内存同步效果
 *
 *
 * Unsuccessful locking and unlocking operations, and reentrant
 * locking/unlocking operations, do not require any memory
 * synchronization effects.
 * 没有成功的加锁和解锁操作,和再次锁和解锁的操作,都不会获得任何内存同步的效果
 *
 * <h3>Implementation Considerations</h3>
 *
 * <p> The three forms of lock acquisition (interruptible,
 * non-interruptible, and timed) may differ in their performance
 * characteristics, ordering guarantees, or other implementation
 * qualities.  Further, the ability to interrupt the <em>ongoing</em>
 * acquisition of a lock may not be available in a given {@code Lock}
 * class.  Consequently, an implementation is not required to define
 * exactly the same guarantees or semantics for all three forms of
 * lock acquisition, nor is it required to support interruption of an
 * ongoing lock acquisition.  An implementation is required to clearly
 * document the semantics and guarantees provided by each of the
 * locking methods. It must also obey the interruption semantics as
 * defined in this interface, to the extent that interruption of lock
 * acquisition is supported: which is either totally, or only on
 * method entry.
 *
 * 实现的思考
 * 锁获取的三种形式(可中断,不可中断和定时)可能有不同的性能特点,保证排序或其他实现特性。
 * 进一步的说,正在进行的获得一个锁具有可中断的能力,给定Lock类可能不可用。
 * 因此,不需要定义一个实现完全相同的所有三种形式的确保或语义获取锁,也不需要中断正在进行锁获取的支持。
 * 一个实现必须提供清楚的文档说明的每一个提供的确保锁定的方法。
 * 它还必须遵守中断语义在这个接口中定义,在一定程度上,支持中断锁获取:可能完全实现,也可能只是个一个方法入口
 *
 *
 * <p>As interruption generally implies cancellation, and checks for
 * interruption are often infrequent, an implementation can favor responding
 * to an interrupt over normal method return. This is true even if it can be
 * shown that the interrupt occurred after another action may have unblocked
 * the thread. An implementation should document this behavior.
 * 中断通常意味着取消,和检查中断通常是罕见的,
 * 一个实现可以支持在正常的方法返回一个中断作为响应。
 * 甚至它真的可以声明,在畅通线程中另一个动作之后可能让中断发生。
 * 一个实现这种行为应该文档。
 *
 * @see ReentrantLock
 * @see Condition
 * @see ReadWriteLock
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface Lock {

    /**
     * Acquires the lock.
     * 获取锁
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     *
     * 如果伙不可用,那么当前线程变的不可用,线程休眠知道锁被获取到
     *
     * <p><b>Implementation Considerations</b>
     *实现思路
     * <p>A {@code Lock} implementation may be able to detect erroneous use
     * of the lock, such as an invocation that would cause deadlock, and
     * may throw an (unchecked) exception in such circumstances.  The
     * circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     * 实现可能实现可以检测到错误的使用锁,
     * 比如调用会导致死锁,和在这种情况下可能抛出未检查异常。
     * 这种情况和异常类型必须在实现里进行记录。
     */
    void lock();

    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     * 除非当前线程是终止的,否则获取锁
     * <p>Acquires the lock if it is available and returns immediately.
     *  如果是可用的,那么获得锁并直接返回
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     *  如果锁不可用,当前线程变成休眠直到以下两种情况发生
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     *锁被当前线程获取到.或者其他线程终止了当前线程,并且终止锁的获取是支持的
     * <p>If the current thread:
     * 如果当前线程
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
     * lock, and interruption of lock acquisition is supported,
     * </ul>
     *
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *在进入此方法的时候或者正在获得锁的时候已经设置终止状态,并且支持中断获取锁
     * <p><b>Implementation Considerations</b>
     *实现思路
     * <p>The ability to interrupt a lock acquisition in some
     * implementations may not be possible, and if possible may be an
     * expensive operation.  The programmer should be aware that this
     * may be the case. An implementation should document when this is
     * the case.
     *中断获取锁的能力在有些实现类里面可能是不可能的,也可能是一个代价比较高的操作
     * 程序猿需要注意这个可能的场景,实现类在这种场景下需要有文档
     * <p>An implementation can favor responding to an interrupt over
     * normal method return.
     *一个实现类可能支持中断普通的方法的返回
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would
     * cause deadlock, and may throw an (unchecked) exception in such
     * circumstances.  The circumstances and the exception type must
     * be documented by that {@code Lock} implementation.
     *实现类可能会检查错误的使用锁,就像执行可能导致死锁的操作,也可能在这种情况下抛出异常
     * 这种场景和异常类型必须在实现类的记录,文档说明
     * @throws InterruptedException if the current thread is
     *         interrupted while acquiring the lock (and interruption
     *         of lock acquisition is supported).
     *
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * Acquires the lock only if it is free at the time of invocation.
     * 获取锁只有在空闲的时候
     * <p>Acquires the lock if it is available and returns immediately
     * with the value {@code true}.
     * If the lock is not available then this method will return
     * immediately with the value {@code false}.
     * 如果锁是可用的直接返回true
     * 如果锁不可用,返回false
     * <p>A typical usage idiom for this method would be:
     * <pre>
     *      Lock lock = ...;
     *      if (lock.tryLock()) {
     *          try {
     *              // manipulate protected state
     *          } finally {
     *              lock.unlock();
     *          }
     *      } else {
     *          // perform alternative actions
     *      }
     * </pre>
     * This usage ensures that the lock is unlocked if it was acquired, and
     * doesn't try to unlock if the lock was not acquired.
     * 这种用法如果获取锁了就去释放锁,不要没获取锁的时候释放锁
     * @return {@code true} if the lock was acquired and
     *         {@code false} otherwise
     */
    boolean tryLock();

    /**
     * Acquires the lock if it is free within the given waiting time and the
     * current thread has not been {@linkplain Thread#interrupt interrupted}.
     * 获取锁如果锁在给定的时间内是可用的,并且当前线程没有终止
     * <p>If the lock is available this method returns immediately
     * with the value {@code true}.
     * 如果锁可用,就直接返回true
     * If the lock is not available then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happens:
     * <ul>
     *     如果锁不可用,当前线程休眠知道以下三中情况发生:
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported; or
     * <li>The specified waiting time elapses
     * </ul>
     *线程获取到了
     * 或者其他线程终止了当前线程 或者等待的时间到了
     * <p>If the lock is acquired then the value {@code true} is returned.
     *如果获取到了,TRUE会被返回
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
     * the lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *如果线程的终止状态被设置了获取锁的获取被终止了,将会抛出异常并且当前线程的终止状态被清除掉
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     *如果知道超时时间,返回false
     * 如果时间小于伙等于0,方法不会等待
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some implementations
     * may not be possible, and if possible may
     * be an expensive operation.
     * The programmer should be aware that this may be the case. An
     * implementation should document when this is the case.
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return, or reporting a timeout.
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances.
     * The circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     *
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @return {@code true} if the lock was acquired and {@code false}
     *         if the waiting time elapsed before the lock was acquired
     *
     * @throws InterruptedException if the current thread is interrupted
     *         while acquiring the lock (and interruption of lock
     *         acquisition is supported)
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Releases the lock.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation will usually impose
     * restrictions on which thread can release a lock (typically only the
     * holder of the lock can release it) and may throw
     * an (unchecked) exception if the restriction is violated.
     * Any restrictions and the exception
     * type must be documented by that {@code Lock} implementation.
     * 释放锁
     * 实现类通常利用严格的那个线程可以释放锁,如果违反会抛出异常
     */
    void unlock();

    /**
     * Returns a new {@link Condition} instance that is bound to this
     * {@code Lock} instance.
     * 返回一个Condition绑带这个锁的实例
     * <p>Before waiting on the condition the lock must be held by the
     * current thread.
     * A call to {@link Condition#await()} will atomically release the lock
     * before waiting and re-acquire the lock before the wait returns.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The exact operation of the {@link Condition} instance depends on
     * the {@code Lock} implementation and must be documented by that
     * implementation.
     *
     * @return A new {@link Condition} instance for this {@code Lock} instance
     * @throws UnsupportedOperationException if this {@code Lock}
     *         implementation does not support conditions
     */
    Condition newCondition();
}
