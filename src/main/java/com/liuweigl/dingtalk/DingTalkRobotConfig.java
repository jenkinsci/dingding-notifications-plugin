package com.liuweigl.dingtalk;

import com.liuweigl.dingtalk.DingTalkSecurityPolicyConfig.DingTalkSecurityPolicyConfigDescriptor;
import com.liuweigl.dingtalk.enums.BuildStatusType;
import com.liuweigl.dingtalk.enums.SecurityPolicyType;
import com.liuweigl.dingtalk.model.BuildMessage;
import com.liuweigl.dingtalk.tools.DingTalkSender;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.User;
import hudson.util.FormValidation;
import hudson.util.FormValidation.Kind;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author liuwei
 * @date 2019/12/25 17:09
 * @desc 机器人配置页面
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuppressWarnings("unused")
public class DingTalkRobotConfig implements Describable<DingTalkRobotConfig> {

  private String id;

  /**
   * 名称
   */
  private String name;

  /**
   * webhook 地址
   */
  private String webhook;

  /**
   * 安全策略配置
   */
  private CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs;

  @DataBoundConstructor
  public DingTalkRobotConfig(String id, String name, String webhook,
      CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs) {
    this.id = id;
    this.name = name;
    this.webhook = webhook;
    this.securityPolicyConfigs = securityPolicyConfigs;
  }

  @Override
  public Descriptor<DingTalkRobotConfig> getDescriptor() {
    return Jenkins.get().getDescriptorByType(DingTalkRobotConfigDescriptor.class);
  }


  @Extension
  public static class DingTalkRobotConfigDescriptor extends Descriptor<DingTalkRobotConfig> {

    /**
     * 安全配置页面
     *
     * @return SecurityPolicyConfigDescriptor
     */
    public DingTalkSecurityPolicyConfigDescriptor getDingTalkSecurityPolicyConfigDescriptor() {
      return Jenkins.get().getDescriptorByType(DingTalkSecurityPolicyConfigDescriptor.class);
    }

    /**
     * 默认 id
     *
     * @return String
     */
    public String getDefaultId() {
      return System.currentTimeMillis() + "";
    }

    /**
     * 获取默认的安全配置选项
     *
     * @return CopyOnWriteArrayList<DingTalkSecurityPolicyConfig>
     */
    public CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> getDefaultSecurityPolicyConfigs() {
      return Arrays
          .stream(SecurityPolicyType.values()).map(DingTalkSecurityPolicyConfig::of)
          .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    /**
     * webhook 字段 1. 必填 2. 唯一
     *
     * @param value webhook
     * @return FormValidation
     */
    public FormValidation doCheckName(@QueryParameter String value) {
      if (StringUtils.isBlank(value)) {
        return FormValidation.error("名称不能为空");
      }
      return FormValidation.ok();
    }

    /**
     * webhook 字段必填
     *
     * @param value webhook
     * @return FormValidation
     */
    public FormValidation doCheckWebhook(@QueryParameter String value) {
      if (StringUtils.isBlank(value)) {
        return FormValidation.error("webhook 不能为空");
      }
      return FormValidation.ok();
    }


    /**
     * 测试配置信息
     *
     * @param id                      id
     * @param name                    名称
     * @param webhook                 webhook
     * @param securityPolicyConfigStr 安全策略
     * @return FormValidation
     */
    public FormValidation doTest(
        @QueryParameter("id") String id,
        @QueryParameter("name") String name,
        @QueryParameter("webhook") String webhook,
        @QueryParameter("securityPolicyConfigs") String securityPolicyConfigStr
    ) {
      CopyOnWriteArrayList<DingTalkSecurityPolicyConfig> securityPolicyConfigs = new CopyOnWriteArrayList<>();
      JSONArray array = (JSONArray) JSONSerializer.toJSON(securityPolicyConfigStr);
      for (Object item : array) {
        JSONObject json = (JSONObject) item;
        securityPolicyConfigs.add(
            new DingTalkSecurityPolicyConfig(
                (Boolean) json.get("checked"),
                (String) json.get("type"),
                (String) json.get("value"),
                ""
            )
        );
      }
      DingTalkRobotConfig robotConfig = new DingTalkRobotConfig(
          id,
          name,
          webhook,
          securityPolicyConfigs
      );
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      DingTalkSender sender = new DingTalkSender(robotConfig);
      String rootUrl = Jenkins.get().getRootUrl();
      User user = User.current();
      if (user == null) {
        user = User.getUnknown();
      }
      String message = sender.send(
          BuildMessage
              .builder()
              .projectName("欢迎使用钉钉机器人插件~")
              .projectUrl(rootUrl)
              .jobName("系统配置")
              .jobUrl(rootUrl + "/configure")
              .statusType(BuildStatusType.SUCCESS)
              .duration("-")
              .executorName(user.getDisplayName())
              .executorMobile(user.getDescription())
              .datetime(formatter.format(System.currentTimeMillis()))
              .build()
      );
      if (message == null) {
        return FormValidation
            .respond(
                Kind.OK,
                "<img src='"
                    + rootUrl
                    + "/images/16x16/accept.png'>"
                    + "<span style='padding-left:4px;color:#52c41a;font-weight:bold;'>测试成功</span>"
            );
      }
      return FormValidation.error(message);
    }
  }
}