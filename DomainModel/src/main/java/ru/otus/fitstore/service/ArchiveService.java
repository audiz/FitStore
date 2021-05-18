package ru.otus.fitstore.service;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class ArchiveService {

    public byte[] zip(Object obj) {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(obj);
            objectOut.close();
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public <T> T unzip(byte[] archive) {
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(archive);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            T polyline = (T) objectIn.readObject();
            objectIn.close();
            return polyline;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
