
	/*
	 *       Copyright© (2018-2019) WeBank Co., Ltd.
	 *
	 *       This file is part of weidentity-java-sdk.
	 *
	 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
	 *       it under the terms of the GNU Lesser General Public License as published by
	 *       the Free Software Foundation, either version 3 of the License, or
	 *       (at your option) any later version.
	 *
	 *       weidentity-java-sdk is distributed in the hope that it will be useful,
	 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
	 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 *       GNU Lesser General Public License for more details.
	 *
	 *       You should have received a copy of the GNU Lesser General Public License
	 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
	 */

package com.webank.weid.util;

	import java.io.IOException;
	import java.io.StringWriter;
	import java.io.Writer;

	import com.fasterxml.jackson.core.JsonGenerationException;
	import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;
	import com.fasterxml.jackson.databind.ObjectMapper;
	import com.fasterxml.jackson.databind.type.TypeFactory;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;

	/**
	 * serialize and deserialize tool.
	 *
	 * @author tonychen 2019年3月20日
	 */
	public class SerializationUtils<T> {

	    private static final Logger logger = LoggerFactory.getLogger(SerializationUtils.class);
	    private static ObjectMapper objectMapper = new ObjectMapper();

	    
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			// TODO Auto-generated method stub

		}
		
		
	    /**
	     * serialize a class instance to Json String.
	     *
	     * @param object the class instance to serialize
	     * @return JSON String
	     */
	    public static String serialize(Object object) {
	        Writer write = new StringWriter();
	        try {
	            objectMapper.writeValue(write, object);
	        } catch (JsonGenerationException e) {
	            logger.error("JsonGenerationException when serialize object to json", e);
	        } catch (JsonMappingException e) {
	            logger.error("JsonMappingException when serialize object to json", e);
	        } catch (IOException e) {
	            logger.error("IOException when serialize object to json", e);
	        }
	        return write.toString();
	    }

	    /**
	     * deserialize a JSON String to an class instance.
	     *
	     * @return class instance
	     */
	    public static <T> T deserialize(String json, Class<T> clazz) {
	        Object object = null;
	        try {
	            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));
	        } catch (JsonParseException e) {
	            logger.error("JsonParseException when serialize object to json", e);
	        } catch (JsonMappingException e) {
	            logger.error("JsonMappingException when serialize object to json", e);
	        } catch (IOException e) {
	            logger.error("IOException when serialize object to json", e);
	        }
	        return (T) object;
	    }


	}

