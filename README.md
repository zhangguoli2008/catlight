# 小猫补光灯 (Catlight)

Android Jetpack Compose 应用，根据产品需求文档实现了屏幕补光 MVP：

- 预设色卡与色轮自由切换，支持十六进制输入与最近使用列表。
- 双亮度控制（颜色亮度 + 窗口亮度）与常亮防息屏。
- 底部抽屉面板、手势控制（单击显隐、双击纯白、长按锁定）。
- 低频微位移防烧屏，遵循“减少动画”系统设置。
- DataStore 持久化最近颜色与偏好设置。

运行方式：

```bash
./gradlew :app:assembleDebug
```

最低支持 Android 8.0（API 26），目标 API 34。
