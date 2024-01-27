package org.wlpiaoyi.framework.utils.security;

import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.Random;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    数据填充
 * {@code @date:}           2024-01-25 09:57:06
 * {@code @version:}:       1.0
 */
class FillTools {

    /**
     * 生成随机数
     * @param length        长度
     * @return: byte[]
     * @author: wlpia
     * @date: 1/25/2024 10:03 AM
     */
    static byte[] randomBytes(int length){
        byte[] rbs = new byte[length];
        int i = 0;
        Random random = new Random();
        while (i < length){
            byte[] rts = ValueUtils.toBytes(Math.abs(random.nextLong()));
            int rtsl = rts.length;
            int rtsi = 0;
            while (rtsi < rtsl){
                if(i >= length){
                    break;
                }
                rbs[i ++] = rts[rtsi ++];
            }
        }
        return rbs;
    }

    static int getFillHeadByteLength(byte[] dataBytes){
        byte[] fil_head_byte = new byte[1];
        fil_head_byte[0] = dataBytes[0];
        return (int) ValueUtils.toLong(fil_head_byte);
    }

    static int getBodyByteLength(byte[] dataBytes, int fil_head_byte_l){
        byte[] fil_body_byte = new byte[2];
        fil_body_byte[0] = dataBytes[fil_head_byte_l + 1];
        fil_body_byte[1] = dataBytes[fil_head_byte_l + 2];
        return (int) ValueUtils.toLong(fil_body_byte);
    }

    static int getDataByteLength(byte[] dataBytes, int fil_head_byte_l){
        byte[] fil_data_byte = new byte[1];
        fil_data_byte[0] = dataBytes[fil_head_byte_l + 3];
        return (int) ValueUtils.toLong(fil_data_byte);
    }

//    static int getFillTailByteLength(byte[] dataBytes, int fil_head_byte_l, int body_l){
//        byte[] fil_tail_byte = new byte[1];
//        fil_tail_byte[0] = dataBytes[fil_head_byte_l + 1 + body_l + 2];
//        return (int) ValueUtils.toLong(fil_tail_byte);
//    }


    /**
     * 填充数据
     * @param dataBytes             被填充数据
     * @param fil_head_byte_l       填充头数据长度
     * @param fil_tail_byte_l       填充尾数据长度
     * @param fil_data_unit_c       填充主数据单元数量
     * @return: byte[]
     * @author: wlpia
     * @date: 1/25/2024 10:00 AM
     */
    static byte[] putIn(byte[] dataBytes, int fil_head_byte_l, int fil_tail_byte_l, int fil_data_unit_c){
        if(fil_head_byte_l > 255){
            throw new BusinessException("fil_head_byte_l can't greater than 255");
        }
        if(fil_tail_byte_l > 255){
            throw new BusinessException("fil_tail_byte_l can't greater than 255");
        }
        //原始数据长度
        final int data_l = dataBytes.length;
        if(fil_data_unit_c > data_l - 1){
            throw new BusinessException("fil_data_unit_c can't greater than " + (data_l - 1));
        }
        //填充主数据长度
        int body_l = data_l + fil_data_unit_c;
        //填充头数据
        final byte[] fil_head_bytes = randomBytes(fil_head_byte_l);
        //填充尾数据
        final byte[] fil_tail_bytes = randomBytes(fil_tail_byte_l);
        //填充尾数据
        final byte[] fil_body_bytes = randomBytes(fil_data_unit_c);
        //总数据长度
        final int fill_data_byte_l = fil_head_byte_l + 1 + fil_tail_byte_l + 1 + body_l + 2 + 1;
        //总数据长度
        byte[] fill_data_bytes = new byte[fill_data_byte_l];

        //填充数据index
        int fill_data_i = 0;
        //填充头数据index
        int fil_head_i= 0;
        fill_data_bytes[fill_data_i ++] = (byte) fil_head_byte_l;
        while (fil_head_i < fil_head_byte_l){
            fill_data_bytes[fill_data_i ++] = fil_head_bytes[fil_head_i ++];
        }

        int body_i = 0;
        int data_i = 0;
        int fil_data_unit_l = data_l / (fil_data_unit_c + 1);
        int fil_data_unit_c_i = 0;
        byte[] body_bytes = new byte[body_l];
        while (body_i < body_l){
            int fil_data_unit_i = 0;
            while (fil_data_unit_i < fil_data_unit_l){
                body_bytes[body_i ++] = dataBytes[data_i ++];
                fil_data_unit_i ++;
            }
            if(fil_data_unit_c_i < fil_data_unit_c){
                body_bytes[body_i ++] = fil_body_bytes[fil_data_unit_c_i ++];
            }else{
                while (data_i < data_l){
                    body_bytes[body_i ++] = dataBytes[data_i ++];
                }
                break;
            }
        }

        if(body_l <= 255){
            fill_data_bytes[fill_data_i ++] = (byte) 0;
            fill_data_bytes[fill_data_i ++] = (byte) body_l;
        }else if(body_l < 255 * 255){
            byte[] body_l_bytes = ValueUtils.toBytes(body_l);
            fill_data_bytes[fill_data_i ++] = body_l_bytes[0];
            fill_data_bytes[fill_data_i ++] = body_l_bytes[1];
        }else{
            throw new BusinessException("body_l can't greater than " + (255 * 255));
        }
        fill_data_bytes[fill_data_i ++] = (byte) data_l;
        body_i = 0;
        while (body_i < body_l){
            fill_data_bytes[fill_data_i ++] = body_bytes[body_i ++];
        }

        //填充头数据index
        int fil_tail_i= 0;
        fill_data_bytes[fill_data_i ++] = (byte) fil_tail_byte_l;
        while (fil_tail_i < fil_tail_byte_l){
            fill_data_bytes[fill_data_i ++] = fil_tail_bytes[fil_tail_i ++];
        }

        return fill_data_bytes;
    }

    /**
     * 抽取数据
     * @param fillDataBytes         已填充的数据
     * @param fil_data_unit_c       填充主数据单元数量
     * @return: byte[]
     * @author: wlpia
     * @date: 1/25/2024 11:16 AM
     */
    static byte[] putOut(byte[] fillDataBytes, int fil_data_unit_c){

        byte[] fil_head_bytes = new byte[1];
        fil_head_bytes[0] = fillDataBytes[0];
        int fil_head_byte_l = (int) ValueUtils.toLong(fil_head_bytes);
        //已填充的数据索引偏移到body的位置
        int fill_data_i = fil_head_byte_l + 1;

        byte[] body_l_bytes = new byte[2];
        body_l_bytes[0] = fillDataBytes[fill_data_i ++];
        body_l_bytes[1] = fillDataBytes[fill_data_i ++];
        fill_data_i ++;
        //填充主数据长度
        int body_l = (int) ValueUtils.toLong(body_l_bytes);
        byte[] body_bytes = new byte[body_l];
        int body_i = 0;
        while (body_i < body_l){
            body_bytes[body_i ++] = fillDataBytes[fill_data_i ++];
        }

        //数据长度
        int data_l = body_l - fil_data_unit_c;
        int fil_data_unit_l = data_l / (fil_data_unit_c + 1);
        int fil_data_unit_c_i = 0;
        byte[] data_bytes = new byte[data_l];
        int data_i = 0;
        body_i = 0;
        while (data_i < data_l){
            if(fil_data_unit_c_i < fil_data_unit_c){
                int fil_data_unit_l_i = 0;
                while (fil_data_unit_l_i < fil_data_unit_l){
                    data_bytes[data_i ++] = body_bytes[body_i ++];
                    fil_data_unit_l_i ++;
                }
                if(fil_data_unit_l_i > 0){
                    fil_data_unit_c_i ++;
                }
                if(fil_data_unit_c_i <= fil_data_unit_c){
                    body_i ++;
                }
            }else{
                data_bytes[data_i ++] = body_bytes[body_i ++];;
            }
        }
        return data_bytes;
    }

}
