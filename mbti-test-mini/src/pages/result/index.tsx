import {View,Image} from '@tarojs/components'
import {AtButton} from 'taro-ui'
import headerBg from "../../assets/headerBg.jpg"
import GlobalFooter from "../../components/GlobalFooter";
import './index.scss'
import Taro from "@tarojs/taro";
import {getBestQuestionResult} from "../../utils/bizUtils";
import questions from '../../data/questions.json';
import question_results from '../../data/question_results.json';
//结果页面
export default () => {
    //获取答案
    const answerList = Taro.getStorageSync('answerList');
    if(!answerList || answerList.length < 1){
      Taro.showToast({
        title:'答案为空',
        icon:'error',
        duration:3000
      })
    }
    //获取测试结果
    const result = getBestQuestionResult(answerList,questions,question_results);
    return (
      <View className='resultPage'>
        <View className='at-article__h1 title'>
          {result.resultName}
        </View>
        <View className='at-article__h2 subTitle'>
          {result.resultDesc}
        </View>
        <AtButton circle className='enterBtn' type='primary' onClick={() => {
          Taro.reLaunch({
            url: '/pages/index/index'
          })
        }}>返回首页</AtButton>
        <Image className='headerBg' src={headerBg}/>
        <GlobalFooter/>
      </View>
    );
};
