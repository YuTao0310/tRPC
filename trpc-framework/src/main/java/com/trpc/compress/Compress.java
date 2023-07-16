package com.trpc.compress;

import com.trpc.extension.SPI;

@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
