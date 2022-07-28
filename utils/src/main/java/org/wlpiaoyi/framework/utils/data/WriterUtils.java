package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class WriterUtils extends DataUtils{

    private static class WriterImpl implements WriterInterface.WriterBytesInterface, WriterInterface.WriterStringInterface{

        private byte[] bytes;
        private String string;

        WriterImpl(byte[] bytes){
            this.bytes = bytes;
        }
        WriterImpl(String string,Charset charset){
            this.string = string;
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public boolean hasNext(int index) {
            return true;
        }

        @Override
        public byte[] writeBytes(int index) {
            return this.bytes;
        }

        @Override
        public String writeString(int index, Charset charset) {
            return this.string;
        }
    }


    public static void append(@NonNull File file, @NonNull WriterInterface.WriterBytesInterface writerInterface) throws IOException {
        Path path = file.toPath();
        DataUtils.makeDir(path.getParent().toString());
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream out = null;
        DataOutputStream dos = null;
        try{
            out = new FileOutputStream(file, true);
            dos = new DataOutputStream(out);
            for (int i = 0; i < writerInterface.getSize(); i++) {
                if(!writerInterface.hasNext(i)) break;
                for (byte b : writerInterface.writeBytes(i)) {
                    dos.write(b);
                }
                dos.flush();
            }
        }finally {
            if(out != null) out.close();
            if(dos != null) dos.close();
        }
    }

    public static void append(@NonNull File file, @NonNull byte[] bytes) throws IOException {
        WriterImpl writer = new WriterImpl(bytes);
        append(file, writer);
    }


    public static void overwrite(@NonNull File file, @NonNull WriterInterface.WriterBytesInterface writerInterface) throws IOException {
        Path path = file.toPath();
        DataUtils.makeDir(path.getParent().toString());
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();
        FileOutputStream out = null;
        DataOutputStream dos = null;
        try{
            out = new FileOutputStream(file);
            dos = new DataOutputStream(out);
            for (int i = 0; i < writerInterface.getSize(); i++) {
                if(!writerInterface.hasNext(i)) break;
                for (byte b : writerInterface.writeBytes(i)) {
                    dos.write(b);
                }
                dos.flush();
            }
        }finally {
            if(out != null) out.close();
            if(dos != null) dos.close();
        }
    }

    public static void overwrite(@NonNull File file, @NonNull byte[] bytes) throws IOException {
        WriterImpl writer = new WriterImpl(bytes);
        overwrite(file, writer);
    }

    public static void overwrite(@NonNull File file,
                                 @NonNull WriterInterface.WriterStringInterface writerInterface,
                                 Charset charset) throws IOException {
        Path path = file.toPath();
        DataUtils.makeDir(path.getParent().toString());
        if(charset == null){
            charset = StandardCharsets.UTF_8;
        }
        BufferedWriter writer = null;
        try{
            writer = Files.newBufferedWriter(path, charset);
            for (int i = 0; i < writerInterface.getSize(); i++) {
                if(!writerInterface.hasNext(i)) break;
                writer.write(writerInterface.writeString(i, charset));
                writer.flush();
            }
        }finally {
            if(writer != null) writer.close();
        }
    }

    public static void overwrite(@NonNull File file,
                                 @NonNull String text,
                                 Charset charset) throws IOException {
        WriterImpl writer = new WriterImpl(text, charset);
        overwrite(file, writer, charset);
    }

    public static void append(@NonNull File file,
                              @NonNull WriterInterface.WriterStringInterface writerInterface,
                              Charset charset) throws IOException {
        Path path = file.toPath();
        DataUtils.makeDir(path.getParent().toString());
        if(charset == null){
            charset = StandardCharsets.UTF_8;
        }

        BufferedWriter writer = null;
        try{
            writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND);
            for (int i = 0; i < writerInterface.getSize(); i++) {
                if(!writerInterface.hasNext(i)) break;
                writer.write(writerInterface.writeString(i, charset));
                writer.flush();
            }
        }finally {
            if(writer != null) writer.close();
        }
    }

    public static void append(@NonNull File file,
                              @NonNull String text,
                              Charset charset) throws IOException {
        WriterImpl writer = new WriterImpl(text, charset);
        append(file, writer, charset);
    }

}
