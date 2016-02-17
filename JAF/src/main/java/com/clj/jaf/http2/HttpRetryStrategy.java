package com.clj.jaf.http2;

/**
 * http重试机制的策略
 *
 */
public interface HttpRetryStrategy {

	/**
	 * 执行重试的逻辑,这些逻辑往往是比较特别的
	 */
	void retry();
}
