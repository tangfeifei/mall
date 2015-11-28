package com.lakecloud.core.zip;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
/**
 * 
* <p>Title: CompressedStream.java</p>

* <p>Description:压缩输出流  </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class CompressedStream extends ServletOutputStream {
	private ServletOutputStream out;
	private GZIPOutputStream gzip;

	/**
	 * 指定压缩缓冲流
	 * 
	 * @param 输出流到压缩
	 * @throws IOException
	 *             if an error occurs with the {@link GZIPOutputStream}.
	 */
	public CompressedStream(ServletOutputStream out) throws IOException {
		this.out = out;
		reset();
	}

	/** @see ServletOutputStream * */
	public void close() throws IOException {
		gzip.close();
	}

	/** @see ServletOutputStream * */
	public void flush() throws IOException {
		gzip.flush();
	}

	/** @see ServletOutputStream * */
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	/** @see ServletOutputStream * */
	public void write(byte[] b, int off, int len) throws IOException {
		gzip.write(b, off, len);
	}

	/** @see ServletOutputStream * */
	public void write(int b) throws IOException {
		gzip.write(b);
	}

	public void reset() throws IOException {
		gzip = new GZIPOutputStream(out);
	}

}
