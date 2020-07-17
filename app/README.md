Ssshiit!
=============

# 개요

몰입캠프 1주차 과제 - 탭 구조를 활용한 안드로이드 앱 제작


# 조원

[이현호](https://github.com/dot0ris)

[임성재](https://github.com/imsj114)

# 설명

## 1. 연락처 탭

- 휴대전화 내부의 연락처를 보여주는 기능, 내부 연락처에 새로 추가하는 기능이 있다.

- ContentProvider를 이용해 휴대전화 내부에 저장된 연락처를 받아온다.

  - 전화번호들이 저장되어 있는 테이블 주소값(Uri)를 받아온 뒤, ContentResolver로 쿼리를 보내 커서를 받아온다.
  
  - 커서 내부의 정보로 custom class를 생성, List에 저장한다. 이후 List를 이름순으로 정렬한다.

  - recyclerView로 연락처 리스트를 보여준다

- alertDialog를 이용하여 사용자로부터 name, number를 받아 내부 연락처에 추가할 수 있다. 

  - ContentProvider를 이용하여 연락처 추가를 구현하였다.
  
  - ContentProviderOperation의 List를 생성한 뒤, List에 RawContacts.CONTENT_URI를 이용하여 raw contact를 넣는다.

  - 이후 Data.CONTECT_URI를 이용하여 name과 number를 넣는다.

- 사용자의 연락처에 접근하기 전에 권한 체크를 하며, 권한이 없을 경우 아무 일도 하지 않는다.
  

## 2. Image Gallery

- 
- Can take a picture and save it.

## 3. Public Toilet Map

