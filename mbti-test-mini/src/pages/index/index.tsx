import {View,Image} from '@tarojs/components'
import {AtButton} from 'taro-ui'
import headerBg from "../../assets/headerBg.jpg"
import GlobalFooter from "../../components/GlobalFooter";
import './index.scss'
import Taro from "@tarojs/taro";
//首页
export default () => {
    return (
      <View className='indexPage'>
        <View className='at-article__h1 title title'>
          MBTI 性格测试
        </View>
        <View className='at-article__h2 title subTitle'>
          只需要 3 分钟，测测你的 MBTI 是什么？
        </View>
        <AtButton circle className='enterBtn' type='primary' onClick={() => {
          Taro.navigateTo({
            url: '/pages/doQuestion/index'
        })
        }}>开始测试</AtButton>
        <Image className='headerBg' src={headerBg}/>
        <GlobalFooter/>
      </View>
    );
};

