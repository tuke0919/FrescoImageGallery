package com.yingke.frescoimagegallery.bean;

import com.yingke.frescoimagegallery.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryBean {

    public int avatar;
    public String nickname;
    public String description;
    public String createtime;
    public List<ImageBean> imageList;

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public List<ImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageBean> imageList) {
        this.imageList = imageList;
    }


    private static final String[] SAMPLE_AVATAR_URI = {
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=201258cbcd80653864eaa313a7dca115/ca1349540923dd54e54f7aedd609b3de9c824873.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=ea218b2c5566d01661199928a729d498/a08b87d6277f9e2fd4f215e91830e924b999f308.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3445377427,2645691367&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2644422079,4250545639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1444023808,3753293381&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=882039601,2636712663&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4119861953,350096499&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2437456944,1135705439&fm=21&gp=0.jpg",
            "http://img4.duitang.com/uploads/item/201506/11/20150611000809_yFe5Z.jpeg"
    };

    private static final int[] SAMPLE_AVATAR_RES = {
            R.drawable.avatar_menghaoran,
            R.drawable.avatar_shasheng,
            R.drawable.avatar_datanghuangdi,
            R.drawable.avatar_zhubajie,
            R.drawable.avatar_libai,
            R.drawable.avatar_dufu,
            R.drawable.avatar_yuanchen,
            R.drawable.avatar_lvbuwei,
            R.drawable.avatar_qinshihuang,
            R.drawable.avatar_change,
            R.drawable.avatar_qianlonghuangdi,
            R.drawable.avatar_wusong,
    };

    private static final String[] SAMPLE_NICK_NAME = {
            "孟浩然",
            "沙僧",
            "大唐皇帝",
            "八戒",
            "李白",
            "杜甫",
            "元嗔",
            "吕不韦",
            "秦始皇",
            "嫦娥",
            "乾隆皇帝",
            "武松"
    };

    private static final String[] SAMPLE_DESCRIPTION = {
            "和摩诘瓶酒,玄宗突然到访，吓死宝宝了。",
            "大师兄，不好了，师傅让妖怪抓走啦了",
            "吾弟玄奘将西去取经，祝一路顺风",
            "发张高老庄的旧照。😁不要以貌取人，哥以前的风采岂是你们懂得？",
            "桃花潭水深千尺，不及汪伦送我情。@汪伦，下次再来赏十里桃花，品万家酒店。",
            "醉眠秋共被，携手日同行。",
            "梦到和乐天同游曲江，还去了慈安寺，惊醒的时候已达到梁州。",
            "《吕氏春秋》已成书，悬赏成功，一字千金，欢迎各大能人异士踊跃参加。",
            "朕诏: 六国已统一，今后统一使用镒合半两，严禁私人铸币。",
            "哎呀，伦家问大家一个问题哦，肿么样才能在朋友圈发纯文字呀，有能告诉我的么😊",
            "朕今天去南巡，碰见几个弹琴的妹纸贼漂亮，叫夏什么的，刚加的微信，明天爆照，(屏蔽：太后，皇后，令妃等3000人)",
            "喝完这碗酒，给老铁们直播打老虎，各位老铁诶我给我点点关注小爱心，抱拳了🙏"
    };

    private static final String[] SAMPLE_CREATETIME = {
            "刚刚",
            "5分钟前",
            "20分钟前",
            "1个小时前",
            "5个小时前",
            "10个小时前",
            "1天前",
            "2天前",
            "5天前",
            "公元1200年10月1日",
            "公元1200年12月10日",
            "公元1201年3月18日"
    };


    /**
     * 获取数据
     * @return
     */
    public static final List<GalleryBean> getDatas() {
        ArrayList<GalleryBean> galleryBeans = new ArrayList<>();
        for (int index = 0; index < SAMPLE_AVATAR_RES.length; index ++) {
            GalleryBean bean = new GalleryBean();
            // 头像，昵称，描述，创建时间
            bean.setAvatar(SAMPLE_AVATAR_RES[index]);
            bean.setNickname(SAMPLE_NICK_NAME[index]);
            bean.setDescription(SAMPLE_DESCRIPTION[index]);
            bean.setCreatetime(SAMPLE_CREATETIME[index]);
            // 随机图片
            ArrayList<ImageBean> imageBeans = new ArrayList<>();
            int random = (int) (Math.random() * ImageBean.SAMPLE_URI_SIZES.length);
            for (int i = 0; i < random; i++) {
                ImageBean imageBean = new ImageBean();
                imageBean.setImgUrl(ImageBean.SAMPLE_URIS[i]);
                imageBean.setWidth(Integer.parseInt(ImageBean.SAMPLE_URI_SIZES[i].split("x")[0]));
                imageBean.setHeight(Integer.parseInt(ImageBean.SAMPLE_URI_SIZES[i].split("x")[1]));
                imageBeans.add(imageBean);
            }
            bean.setImageList(imageBeans);
            galleryBeans.add(bean);
        }
        return galleryBeans;
    }

}
