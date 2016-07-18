package com.qiandai.pay.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.beans.BeanCopier;

/**
 * Created by mashengli on 2016/7/18.
 */
public class BeanCopierUtil {
    private static Logger logger = LoggerFactory
            .getLogger(BeanCopierUtil.class);

    /**
     * convert input object to an object of output type
     * no validation in side, so please make sure all arguments are ok
     * before invoke this method
     * @param inputObj
     * @param outputType
     * @return
     */
    public static <I, O> O convert(I inputObj, Class<O> outputType) {
        try {
            if(inputObj == null){
                return null;
            }
            O k = outputType.newInstance();
//			UFQBeanUtils.copyPropertiesIgnoreNullValue(inputObj, k);

            BeanCopier copier = BeanCopier.create(inputObj.getClass(), outputType, false);
            copier.copy(inputObj, k, null);
            return k;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * convert input list to a list of output type
     * no validation in side, so please make sure all arguments are ok
     * before invoke this method
     * @param inputList
     * @param outputType
     * @return
     */
    public static <I, O> List<O> convert(List<I> inputList, Class<O> outputType) {
        if(inputList == null){
            return null;
        }
        List<O> outputList = new ArrayList<>(inputList.size());
        for(I i: inputList){
            O convert = convert(i, outputType);
            outputList.add(convert);
        }
        return outputList;
    }

    /**
     * convert input list to a list of output type
     * no validation in side, so please make sure all arguments are ok
     * before invoke this method
     * @param inputMap
     * @param outputType
     * @return
     */
    public static <I, O> Map<String, List<O>> convert(Map<String, List<I>> inputMap,  Class<O> outputType) {
        if(inputMap == null){
            return null;
        }
        Map<String, List<O>> map = new HashMap<String, List<O>>();
        Set<Map.Entry<String, List<I>>> entrySet = inputMap.entrySet();
        for(Map.Entry<String, List<I>> entry: entrySet){
            map.put(entry.getKey(), convert(entry.getValue(), outputType));
        }
        return map;
    }
}
