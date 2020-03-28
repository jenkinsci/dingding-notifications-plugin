package io.jenkins.plugins.model;

import com.dingtalk.api.request.OapiRobotSendRequest.At;
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import io.jenkins.plugins.enums.MsgTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * @author liuwei
 * @date 2020/3/27 17:05
 * @desc 消息的通用信息
 */
@Data
@Builder
public class MessageModel {

  /**
   * 消息类型
   */
  private MsgTypeEnum type;

  /**
   * 需要 at 的手机号码
   */
  private Set<String> atMobiles;

  /**
   * 是否 at 全部
   */
  private Boolean atAll;

  /**
   * 标题，首屏会话透出的展示内容
   */
  private String title;

  /**
   * 消息正文
   */
  private String text;

  /**
   * 点击消息跳转的URL
   */
  private String messageUrl;

  /**
   * 图片URL
   */
  private String picUrl;


  /**
   * 单个按钮的方案。(设置此项和singleURL后btns无效)
   */
  private String singleTitle;

  /**
   * 点击singleTitle按钮触发的URL
   */
  private String singleUrl;

  /**
   * 按钮的信息：title-按钮方案，actionURL-点击按钮触发的URL
   */
  private List<ButtonModel> btns;

  /**
   * 0-按钮竖直排列，1-按钮横向排列
   */
  private String btnOrientation;

  /**
   * 0-正常发消息者头像，1-隐藏发消息者头像
   */
  private String hideAvatar;


  /**
   * title 不能为空
   *
   * @return 带默认值的标题
   */
  public String getTitle() {
    if (title == null) {
      return "Jenkins 构建通知";
    }
    return title;
  }

  /**
   * 获取 at 设置
   *
   * @return at
   */
  public At getAt() {
    At at = new At();
    if (atMobiles != null) {
      at.setAtMobiles(new ArrayList<>(atMobiles));
    }
    at.setIsAtAll(String.valueOf(atAll));
    return at;
  }

  /**
   * 获取按钮列表
   *
   * @return 按钮列表
   */
  public List<Btns> getBtns() {
    if (btns == null) {
      return null;
    }
    ArrayList<Btns> btnList = new ArrayList<>();
    for (ButtonModel item : btns) {
      Btns btn = new Btns();
      btn.setTitle(item.getTitle());
      btn.setActionURL(item.getActionUrl());
      btnList.add(btn);
    }
    return btnList;
  }

}
