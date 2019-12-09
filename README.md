# mlgrid
機械学習サービス基盤

## 基盤開発

* ストリーミングプロトコル
* データ処理機構(リソースサービス + 処理サービス → 新しいリソースサービス)
* GPU利用管理(当面は学習系サービス1, 推論系サービス1)
  * モニタリング

## サービス候補

* リソースサービス
  * Wikipedia京都コーパス(単言語)
  * Wikipedia京都コーパス(対訳)
* トークナイザサービス
  * SentencePiece Wikipedia京都コーパス(日)
  * SentencePiece Wikipedia京都コーパス(英)
  * SentencePiece Wikipedia京都コーパス(日英)
  * Mecab(日)
  * Juman++(日)
* トークナイザ学習サービス
  * SentencePiece
* 単語分散表現サービス
  * Word2Vec Wikipedia京都コーパス(日)
  * Word2Vec Wikipedia京都コーパス(英)
  * BERT(たぶん)
* 単語分散表現学習サービス
  * Word2Vec
  * BERT(たぶん)
* 翻訳サービス
  * OpenNMT-tf Wikipedia京都コーパス
  * OpenNMT-py Wikipedia京都コーパス
* 翻訳学習サービス
  * OpenNMT-tf
  * OpenNMT-py
* 画像認識サービス
  * Keras VGG16(imagenet)
  * Keras VGG19(imagenet)
  * Keras Xception(imagenet)
  * Keras EfficientNetB0〜B7(imagenet)
* 画像認識学習サービス
  * Keras
* 画像認識特徴点可視化サービス
  * SHAP
  * GradCam
  * GradCam+GuidedBackpropagation
* 物体検出サービス
  * YoloV3
  * Gaussian YoloV3
  * M2Det
  * MobileNetV3SSD
* 物体検出学習サービス
  * YoloV3
  * Gaussian YoloV3
  * M2Det
  * MobileNetV3SSD
  
  
