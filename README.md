# score-batch

実際に馬券を買ったときをシミュレーションするバッチ

# キャッシュクリア　
```declarative
redis-cli KEYS "race*" | xargs redis-cli DEL
```