package com.dicelab.whopuppy.data

// ‘강원도‘,’경기‘,’경상남도‘,’경상북도‘,’광주‘,’대구‘,’대전,‘’부산‘,’서울‘,’세종‘,’울산‘,’인천‘,’전라남도‘,’전라북도‘,’제주도‘,’충청남도‘,’충청북도'
enum class Area(val areaName: String) {
    ALL("전체"),
    GANGWONDO("강원도"),
    GYEONGGIDO("경기"),
    GYEONGSANGNAMDO("경상남도"),
    GYEONGSANGBUKDO("경상북도"),
    GWANGJU("광주"),
    DAEGU("대구"),
    DAEJEON("대전"),
    BUSAN("부산"),
    SEOUL("서울"),
    SEJONG("세종"),
    ULSAN("울산"),
    INCHEON("인천"),
    JEOLLANAMDO("전라남도"),
    JEOLLABUKDO("전라북도"),
    JEJUISLAND("제주도"),
    CHUNGCHEONGNAMDO("충청남도"),
    CHUNGCHEONGBUKDO("충청북도");
}
