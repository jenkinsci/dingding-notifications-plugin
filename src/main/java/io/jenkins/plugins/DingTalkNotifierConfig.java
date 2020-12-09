package io.jenkins.plugins;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import io.jenkins.plugins.enums.NoticeOccasionEnum;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author liuwei
 * @date 2019/12/28 11:06
 */
@Getter
@Setter
@ToString
public class DingTalkNotifierConfig extends AbstractDescribableImpl<DingTalkNotifierConfig> {

  private boolean checked;

  private String robotId;

  private String robotName;

  private String atMobile;

  private String content;

  private Set<String> noticeOccasions;

  public Set<String> getNoticeOccasions() {
    return noticeOccasions == null
        ? DingTalkGlobalConfig.get().getNoticeOccasions()
        : noticeOccasions;
  }

  public Set<String> getAtMobiles() {
    if (StringUtils.isEmpty(atMobile)) {
      return new HashSet<>(16);
    }
    return Arrays.stream(StringUtils.split(atMobile, "\n")).collect(Collectors.toSet());
  }

  public String getContent() {
    return content == null ? "" : content;
  }

  @DataBoundConstructor
  public DingTalkNotifierConfig(
      boolean checked, String robotId, String robotName, String atMobile, String content) {
    this.checked = checked;
    this.robotId = robotId;
    this.robotName = robotName;
    this.atMobile = atMobile;
    this.content = content;
  }

  public DingTalkNotifierConfig(DingTalkRobotConfig robotConfig) {
    this(false, robotConfig.getId(), robotConfig.getName(), null, "");
  }

  @Extension
  public static class DingTalkNotifierConfigDescriptor extends Descriptor<DingTalkNotifierConfig> {
    /**
     * 通知时机列表
     *
     * @return 通知时机
     */
    public NoticeOccasionEnum[] getNoticeOccasionTypes() {
      return NoticeOccasionEnum.values();
    }
  }
}
