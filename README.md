DynamicTableView
==========

[![Java CI](https://github.com/k7t3/DynamicTableView/actions/workflows/test.yaml/badge.svg)](https://github.com/k7t3/DynamicTableView/actions/workflows/test.yaml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

幅に応じて動的に列数を変更するJavaFXコントロールです。

![sample](https://user-images.githubusercontent.com/33083609/151005827-ff90e37a-73ad-4656-8808-bf62d968e48e.gif)



コントロールではありませんが、同様のレイアウトを表現できるFlowPaneと比較すると
以下のような特徴があります。
* VirtualFlowによる効率的な描画
* SelectionModelを利用することによる要素の選択ロジック

これらの特徴は内部的にTableViewを利用することで実装しています。

## How to use
OpenJFX 11以上で動作します。

Maven Central Repository
```groovy
implementation group: 'io.github.k7t3', name: 'DynamicTableView', version: 'X.X.X'
```

DynamicTableViewコントロールの利用方法は以下の通り、要素を追加することで表示されます。
```java
DynamicTableView<String> tableView = new DynamicTableView<>();
tableView.getItems().addAll(
    "apple",
    "lemon",
    "orange",
    "raspberry",
    "cherry",
    "banana",
    "peach",
    "strawberry",
    "pineapple"
);
```

<img width="540" alt="default" src="https://user-images.githubusercontent.com/33083609/184387411-1d7def9e-efe4-4c1e-94fb-41f30ec956f6.png">



DynamicTableViewに任意のNodeを表示させるには、DynamicTableCellを継承したクラスを作成します。
```java
class BaseballTeamCell extends DynamicTableCell<BaseballTeam> {

    public BaseballTeamCell() {
        super();
    }

    private Label teamName;
    private ImageView teamImage;

    @Override
    protected Node createView() {
        teamName = new Label();
        teamImage = new ImageView();
        teamImage.setPreserveRatio(true);
        teamImage.fitWidthProperty().bind(prefCellWidthProperty().multiply(0.8));
        teamImage.fitHeightProperty().bind(prefCellHeightProperty().multiply(0.8));

        BorderPane layout = new BorderPane();
        layout.setTop(teamName);
        layout.setCenter(teamImage);
        return layout;
    }

    @Override
    protected void updateItem(BaseballTeam item) {
        teamName.setText(item.getName());
        teamImage.setImage(item.getImage());
    }
}
```

作成したセルクラスを生成するセルファクトリを指定することでカスタムセルが利用できます。
```java
DynamicTableView<BaseballTeam> tableView = new DynamicTableView<>();
// セルファクトリを指定
tableView.setCellFactory(BaseballTeamCell::new);
tableView.setCellWidth(200);
tableView.setCellHeight(200);
tableView.getItems().addAll(getAllProfessionalTeams());
```

<img width="734" alt="images" src="https://user-images.githubusercontent.com/33083609/184388039-d9cee87b-4921-4bbd-9949-177b100a8b67.png">



## 既知の不具合
OpenJFX 17にて、複数選択しているセルを再度クリックすることで以下の例外が標準出力にプリントされます。
```text
Exception in thread "JavaFX Application Thread" java.lang.IllegalArgumentException: fromIndex(0) > toIndex(-1)
```

これは
[OpenJDKのBTS](https://bugs.openjdk.java.net/browse/JDK-8273324 "IllegalArgumentException: fromIndex(0) > toIndex(-1) after clear and select TableCell")
に既に報告されており、OpenJFX 18で修正されるようです。




## Licence

    Copyright 2022 k7t3
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.