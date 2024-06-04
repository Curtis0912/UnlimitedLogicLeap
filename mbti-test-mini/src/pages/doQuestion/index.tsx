import {View} from '@tarojs/components'
import {AtButton, AtProgress, AtRadio} from "taro-ui";
import {useEffect, useState} from "react";
import Taro from "@tarojs/taro";
import questions from "../../data/questions.json";
import './index.scss'
//答题页面
export default () => {

  //当前题目序号（从1开始）
  const [current, setCurrent] = useState<number>(1);
  //当前题目
  const [currentQuestion, setCurrentQuestion] = useState(questions[0]);
  //当前答案
  const [currentAnswer, setCurrentAnswer] = useState<string>();
  //回答列表
  const [answerList] = useState<string[]>([]);
  //遍历选项用map读取
  const questionOptions = currentQuestion.options.map((option) => {
    return {
      label: `${option.key}.${option.value}`,//展示内容
      value: option.key//用户选中后向后端发送的值
    }
  })

  //序号变化时，切换当前题目和当前回答
  useEffect(() => {
    setCurrentQuestion(questions[current - 1])//需要-1，题目从1开始
    setCurrentAnswer(answerList[current - 1])
  }, [current]);//deps放主动态变量，当current变化时，重新执行useEffect

  return (
    <View className='doQuestionPage'>
      <AtProgress percent={(current / questions.length) * 100} isHidePercent />

      {/*{JSON.stringify(answerList)}*/}
      <View className='at-article__h2 title'>{current}、{currentQuestion.title}</View>
      <View className='options-wrapper'>
        <AtRadio
          options={questionOptions}
          value={currentAnswer}
          onClick={(vaule) => {
            setCurrentAnswer(vaule);//用户选中某个选项时 要改变当前回答的值
            answerList[current - 1] = vaule;//记录当前回答的答案
            if(current < questions.length){
              setCurrent(current + 1);
            }else{
              setCurrent(current);
            }
          }}
        />
      </View>
      {/*{//当前题目序号小于总题目数时显示下一题按钮*/}
      {/*  current < questions.length &&*/}
      {/*  <AtButton circle className='chooseBtn' type='primary' disabled={!currentAnswer} onClick={() => setCurrent(current + 1)}>下一题</AtButton>*/}
      {/*}*/}

      {//当前题目序号为总题目数时显示查看结果按钮
        current == questions.length &&
        <AtButton circle className='chooseBtn' type='primary' disabled={!currentAnswer} onClick={() => {
          //传递答案
          Taro.setStorageSync("answerList", answerList);
          // todo 跳转到结果页面
          Taro.navigateTo({
            url: '/pages/result/index'
          })
        }}>查看结果</AtButton>
      }
      {//当前题目序号大于1时显示上一题按钮
        current > 1 &&
        <AtButton circle className='chooseBtn' onClick={() => setCurrent(current - 1)}>上一题</AtButton>
      }
    </View>
  );
};

