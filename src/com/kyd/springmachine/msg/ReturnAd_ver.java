package com.kyd.springmachine.msg;

import java.util.List;

public class ReturnAd_ver {


    /**
     * result : 0
     * detail_data  : [{"id":1,"soure_name":"电影","path_url":"http://www.baidu.com","soure_type":"movie","playtime":30},{"id":2,"soure_name":"图片","path_url":"http://img1.3lian.com/2015/a2/246/d/51.jpg","soure_type":"pic","playtime":10}]
     */

    private int result;

    private List<detailDataBean> detail_data;
    private String msg;

    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public List<detailDataBean> getDetail_data() {
		return detail_data;
	}

	public void setDetail_data(List<detailDataBean> detail_data) {
		this.detail_data = detail_data;
	}

	public static  class detailDataBean {
        /**
         * id : 1
         * soure_name : 电影
         * path_url : http://www.baidu.com
         * soure_type : movie
         * playtime : 30
         */

        private int id;
        private String soure_name;
        private String path_url;
        private String soure_type;
        private int playtime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSoure_name() {
            return soure_name;
        }

        public void setSoure_name(String soure_name) {
            this.soure_name = soure_name;
        }

        public String getPath_url() {
            return path_url;
        }

        public void setPath_url(String path_url) {
            this.path_url = path_url;
        }

        public String getSoure_type() {
            return soure_type;
        }

        public void setSoure_type(String soure_type) {
            this.soure_type = soure_type;
        }

        public int getPlaytime() {
            return playtime;
        }

        public void setPlaytime(int playtime) {
            this.playtime = playtime;
        }
    }

}
