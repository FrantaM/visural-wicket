/*
 *  Copyright 2010 Richard Nichols.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.visural.wicket.util.lesscss;

import com.visural.common.web.lesscss.LessCSS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.resource.locator.ResourceStreamLocator;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

/**
 * A stream locator that automatically runs ".less" files through the LessCSS
 * compilation process.
 * 
 * @version $Id: LessCSSResourceStreamLocator.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class LessCSSResourceStreamLocator extends ResourceStreamLocator {
    private static final long serialVersionUID = 1L;
    
    private Map<CacheBean, CacheResponse> cache = new ConcurrentHashMap<CacheBean, CacheResponse>();

    public LessCSSResourceStreamLocator() {
    }

    public LessCSSResourceStreamLocator(final IResourceFinder finder) {
        super(finder);
    }

    @Override
    public IResourceStream locate(Class<?> clazz, String path) {
        IResourceStream orig = super.locate(clazz, path);
        if (orig == null) {
            return null;
        }
        if (path.endsWith(".less")) {
            CacheBean cb = new CacheBean(clazz, path, null, null, null);
            if (cache.get(cb) == null) {
                try {
                    cache.put(cb, getResponse(orig));
                } catch (Exception ex) {
                    Logger.getLogger(LessCSSResourceStreamLocator.class.getName()).log(Level.SEVERE, null, ex);
                    throw new WicketRuntimeException("Failed running LessCSS", ex);
                }
            }
            return new LessResourceStream(cache.get(cb), super.locate(clazz, path));
        } else {
            return orig;
        }
    }


    @Override
    public IResourceStream locate(Class<?> clazz, String path, String style, String variation, Locale locale, String extension, boolean strict) {
        IResourceStream orig = super.locate(clazz, path, style, variation, locale, extension, strict);
        if (orig == null) {
            return null;
        }
        if (path.endsWith(".less")) {
            CacheBean cb = new CacheBean(clazz, path, style, locale, extension);
            if (cache.get(cb) == null) {
                try {
                    cache.put(cb, getResponse(orig));
                } catch (Exception ex) {
                    Logger.getLogger(LessCSSResourceStreamLocator.class.getName()).log(Level.SEVERE, null, ex);
                    throw new WicketRuntimeException("Failed running LessCSS", ex);
                }
            }
            return new LessResourceStream(cache.get(cb), orig);
        } else {
            return orig;
        }
    }

    private CacheResponse getResponse(IResourceStream stream) throws ResourceStreamNotFoundException, IOException {
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r;
        InputStream in = stream.getInputStream();
        while ((r = in.read()) != -1) {
            baos.write(r);
        }
        stream.close();
        data = baos.toByteArray();
        LessCSS engine = new LessCSS();
        String lesscss = engine.less(new ByteArrayInputStream(data));
        CacheResponse cr = new CacheResponse(lesscss.getBytes().length, lesscss);
        return cr;
    }

    class LessResourceStream implements IResourceStream {
        private final CacheResponse cr;
        private final IResourceStream regular;

        public LessResourceStream(CacheResponse cr, IResourceStream regular) {
            this.cr = cr;
            this.regular = regular;
        }

        public String getContentType() {
            return "text/css";
        }

        public Bytes length() {
            return Bytes.bytes(cr.getLength());
        }

        public InputStream getInputStream() throws ResourceStreamNotFoundException {
            return new ByteArrayInputStream(cr.getData().getBytes());
        }

        public void close() throws IOException {
        }

        public Locale getLocale() {
            return regular.getLocale();
        }

        public void setLocale(Locale locale) {
            regular.setLocale(locale);
        }

        public Time lastModifiedTime() {
            return regular.lastModifiedTime();
        }

        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setStyle(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getVariation() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setVariation(String string) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class CacheResponse implements Serializable {
        private final long length;
        private final String data;

        public CacheResponse(long length, String data) {
            this.length = length;
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public long getLength() {
            return length;
        }
        
    }

    class CacheBean implements Serializable {
        private final Class<?> clazz;
        private final String path;
        private final String style;
        private final Locale locale;
        private final String extension;

        public CacheBean(Class<?> clazz, String path, String style, Locale locale, String extension) {
            this.clazz = clazz;
            this.path = path;
            this.style = style;
            this.locale = locale;
            this.extension = extension;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getExtension() {
            return extension;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getPath() {
            return path;
        }

        public String getStyle() {
            return style;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CacheBean other = (CacheBean) obj;
            if (this.clazz != other.clazz && (this.clazz == null || !this.clazz.equals(other.clazz))) {
                return false;
            }
            if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
                return false;
            }
            if ((this.style == null) ? (other.style != null) : !this.style.equals(other.style)) {
                return false;
            }
            if (this.locale != other.locale && (this.locale == null || !this.locale.equals(other.locale))) {
                return false;
            }
            if ((this.extension == null) ? (other.extension != null) : !this.extension.equals(other.extension)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
            hash = 11 * hash + (this.path != null ? this.path.hashCode() : 0);
            hash = 11 * hash + (this.style != null ? this.style.hashCode() : 0);
            hash = 11 * hash + (this.locale != null ? this.locale.hashCode() : 0);
            hash = 11 * hash + (this.extension != null ? this.extension.hashCode() : 0);
            return hash;
        }


    }
}
