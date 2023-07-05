# MadGPT

---

Flow Week1 4분반 병찬,경민팀

- chat gpt api를 이용하여 이미지 generation, sound to text로 검색기능, chat 검색기능이 가능합니다
- 연락처를 받아와서 search,add,delete,상세정보창에서 전화,메세지,이메일보내기가 가능합니다
- 갤러리에서 local image , 갤러리 image, camera 촬영후 이미지를 추가하고 삭제가 가능합니다

---

### a. 개발 팀원

- 김경민 - 한양대학교 컴퓨터소프트웨어학부 19학번
- 박병찬 - KAIST 전산학부 21학번

---

### b. 개발환경

- Language: Kotlin
- OS: Android

```
minSdkVersion 24
targetSdkVersion 33
```

- IDE: Android Studio
- Target Device: Galaxy S7

---

### c. 어플리케이션 소개

### 0.MainActivity

***Major features***

- 도형 두개가 좌우에서 날아오는 splash 화면을 구현하였습니다
- 화면에서 스와이프를 하면 다른 탭으로 view가 전환됩니다
- 또는 아래쪽의 탭을 눌러 view를 전환할 수 있습니다

---

***기술설명***

- ViewPager2와 TabLayout을 사용하여 스와이프를 통해 탭이 자연스럽게 전환될 수 있도록 하였습니다.
- 스플래시 구현을 위해 액티비티 간의 전환을 처리하였으며, 시작 액티비티를 메인액티비티에서 스플래시액티비티로 전환했습니다.
- splash 구현에서 objectAnimator를 사용하여 두 개의 이미지를 좌우에서 날아오게 애니메이션을 처리하였습니다
- Handler를 이용해 일정시간 지연 후 작업이 실행되게 하였습니다.

![Untitled](MadGPT%2074d5057fffc24e1081befe175345e064/Untitled.png)

![Untitled](MadGPT%2074d5057fffc24e1081befe175345e064/Untitled%201.png)

- 앱에서 사용하는 모든 이미지 소스는 xml과 피그마, PPT, 포토샵을 이용해 직접 제작했으며, 이를 통해 일관된 디자인을 구현하였습니다.

---

### TAB 1 - 연락처

![Screenshot_20230705-191341_Madcamp (1).jpg](MadGPT%2074d5057fffc24e1081befe175345e064/Screenshot_20230705-191341_Madcamp_(1).jpg)

![Screenshot_20230705-191354_Madcamp (1).jpg](MadGPT%2074d5057fffc24e1081befe175345e064/Screenshot_20230705-191354_Madcamp_(1).jpg)

***Major features***

- initial의 자음으로 그룹별로 연락처에 나타납니다.
- 우측 상단에 Add Contact 버튼을 클릭하여 연락처를 추가할 수 있는 화면으로 이동할 수 있습니다.
- 연락처를 클릭하면 상세 정보창으로 이동할 수 있습니다.
- 상세정보창에서 이메일, 전화, 메세지 버튼을 누르면 해당 기능을 수행할 수 있습니다.
- search contacts에서 찾고자 하는 연락처를 filtering 할 수 있습니다.
- 연락처를 길게 누를시에 삭제할 수 있습니다.

---

***기술설명***

- RecyclerView를 사용하여 목록 형태의 인터페이스를 구현했습니다.
- intent를 이용해 핸드폰의 연락처 앱에서 이름, 전화번호, 이메일을 불러왔습니다.
- intent를 이용해 전화, 문자, 이메일 버튼을 눌렀을때 해당 연락처의 정보가 바로 입력될 수 있도록 했습니다.
- 연락처 삭제를 구현하기 위해 전역변수를 선언하여 삭제된 연락처를 저장했습니다.

---

### TAB 2 - 갤러리

![Screenshot_20230705-191421_Madcamp.jpg](MadGPT%2074d5057fffc24e1081befe175345e064/Screenshot_20230705-191421_Madcamp.jpg)

***Major features***

- local 이미지가 기본적으로 화면에 나타납니다
- 오른쪽위의 Add Image를 누르면 카메라와 갤러리 팝업창이 뜹니다
- 갤러리를 누르고 사진을 선택하면 해당 사진이 추가됩니다
- 카메라를 누르고 사진을 촬영후 확인을 누르면 해당 사진이 추가됩니다
- 사진을 짧게 누르면 사진이 화면크기에 맞게 확대되고 화면을 누르거나 뒤로가기를 한번 누르면 갤러리로 돌아옵니다
- 사진을 길게 눌러서 삭제할 수 있습니다

---

***기술설명***

- RecyclerView를 사용하여 목록 형태의 인터페이스를 구현했습니다.
- recyclerview의 layoutmanager를 grid layoutmanager로 하고 squareFramelayout이라는 custom layout을 만들어서 이미지를 정사각형으로 보여지게 하였습니다.
- local 이미지는 Int로 갤러리 이미지는 uri로 카메라 이미지는 bitmap으로 datas에 저장하여 처리하였습니다
- Glide 라이브러리를 사용하여 이미지를 로드하고 보여주었습니다.
- Android Camera API를 사용하여 카메라로부터 사진을 캡처했습니다.
- Android Storage API를 사용하여 갤러리에서 이미지를 선택하고 로드했습니다

---

### TAB 3 - GPT

![Screenshot_20230705-191549_Madcamp (1).jpg](MadGPT%2074d5057fffc24e1081befe175345e064/Screenshot_20230705-191549_Madcamp_(1).jpg)

![Screenshot_20230705-191617_Madcamp (1).jpg](MadGPT%2074d5057fffc24e1081befe175345e064/Screenshot_20230705-191617_Madcamp_(1).jpg)

***Major features***

- text를 입력후 사진 버튼을 누르면 해당 text에 해당하는 이미지를 generation 해서 보여줍니다
- 마이크를 누르면 녹음이 시작되고 정지버튼을 누르면 해당 음성이 text로 변환되어 자동으로 chat을 할 수 있습니다
- text 입력후 채팅 버튼을 눌러 chat을 할 수 있습니다

---

***기술설명***

- openAI사에서 제공하는 api를 연동하여 gpt 서버와의 상호작용을 구현하였습니다.
- 채팅 형태의 상호작용을 위해 openAI사의 api에서 제공하는 모델 중 gpt3.5-turbo 모델을 사용하였으며, 연속적인 대화의 흐름을 파악하여 답변할 수 있도록 하였습니다.
- 음성 형태의 상호작용을 위해 openAI사의 api에서 제공하는 모델 중 whispher-1 모델을 사용하였으며, 이를 통해 음성을 텍스트로 변환한 후 gpt 모델에 넣어 결과를 얻어냅니다.
- 이미지 생성을 위해 openAI사의 api에서 제공하는 모델 중 DALLE-1 모델을 사용하였으며, 이를 통해 텍스트를 바탕으로 이미지를 생성하고 서버의 이미지 url을 전달 받았습니다.
- 위에서 전달받은 url을 화면에 띄우기 위해 ~~
- 음성 녹음을 위해 특정한 코덱과 형식으로 내부 저장소에 파일을 쓸 수 있도록 구현했습니다.
