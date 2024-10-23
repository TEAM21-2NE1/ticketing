# 🎫 2NET-CKET: 대규모 트래픽 처리 예매 시스템 🎫

![21조-542266](https://github.com/user-attachments/assets/2c4556be-2e05-4766-a69e-1d7a032da060)

## 🔎 1. 프로젝트 소개

Java, Spring Boot 기반 백엔드 팀 프로젝트 입니다.<br>MSA 구조의 예매 시스템이며, 특정 시간에 사용자의 요청이 몰리는 온라인 예매 시스템에서 대규모 트래픽을 안정적으로 처리하는 것을 목표로 합니다. 
티켓 예매 시, 예매부터 결제까지의 과정에서 동시성 이슈 관리를 안정적으로 처리하여, 높은 사용자 경험을 제공할 것입니다.

<br>

## 🎯2. 프로젝트 목표

- 마이크로 서비스 아키텍처(**MSA**) 설계
- **대용량 트래픽에도 안정적인 서비스**
    
    Redis를 이용하여 예매 대기열을 구현하고, Jmeter를 통해 부하 테스트를 진행
    
- **메시지 처리의 병렬성을 고려한 Kafka 설정**
- **서비스 장애 방지를 위해 모니터링 시스템 구축**
    
    Prometheus와 Grafana를 연동하여  마이크로서비스 아키텍처에서 각 서비스의 성능 및 상태를 실시간으로 메트릭을 수집하고 모니터링하여 장애를 사전에 감지하여 대응

<br>

## 🛠️ 3. 아키텍처 및 ERD
<details>
  <summary>아키텍처</summary>
  
  ![21조_아키텍처-인프라-3](https://github.com/user-attachments/assets/898b3c17-78a3-47a2-aaf7-7bcc72234569)
  
</details>

<details>
  <summary>ERD</summary>
  
  ![21조 ERD](https://github.com/user-attachments/assets/f75a7430-730c-4215-ace5-e7efb485aef7)
  
</details>

<br>

## ✅ 4. 주요 기능

### 🎪공연

- **공연 등록하기**
    - **공연 관계자만** 공연을 등록할 수 있다.
    - 공연 등록 시에 공연 오픈 시간을 등록한다. 해당 오픈 시간 이전에는 일반 사용자는 해당 공연을 조회할 수 없으며, 공연 관계자만 조회할 수 있다.
    - AWS S3를 이용해 이미지 저장 및 관리
- **공연 조회하기**
    - 공연 오픈 시간이 지나면 모든 사용자들은 공연을 조회할 수 있다.
- **공연 랭킹 조회**
    - redis와 scheduler를 이용해 랭킹 조회 성능을 개선하였다.
- **공연 삭제**
    - kafka를 이용해 비동기로 관련 데이터(리뷰) 삭제한다

### 🎟️예매

- **대기열을 통한 예매**
    - 예매는 티켓 예매 시간 이후부터 가능하다.
    - 좌석은 동시 선택이 불가능하다.
    - 좌석 조회 시, running queue에 자리가 없으면 대기열(waiting queue)에 들어가게 된다.
    - 사용자가 대기열에 진입한 후 새로고침 했을 때, 기존 대기번호를 유지하지 않고 대기열의 끝으로 이동하여 새로운 대기번호를 부여한다.
      <details>
        <summary>예매 flow</summary>

      ![21조_아키텍처-예매 플로우 drawio](https://github.com/user-attachments/assets/efd2aa36-1dd1-4ce2-b376-a6eb85324de3)
  
      </details>
        
- **좌석 선택**
    - redisson 분산락을 이용해 좌석 선택시 동시성 이슈 해결한다.
    - redis keyspace notification 을 이용해 좌석 선택 제한시간 초과 시 좌석의 상태를 'AVAILABLE'로 변경한다.

### 💸결제

- **결제하기**
    - 좌석 선택을 완료한 후 결제를 진행한다.
    - 결제 완료시, 좌석과 주문상태가 db에 최종적으로 업데이트 된다.
    - portOne 을 이용해 외부 결제 연동을 진행한다.

### 📝리뷰

- **리뷰 조회**
    - **Redis Cache**를 적용해 리뷰를 빠르게 조회할 수 있다.
    - 리뷰 목록 조회 시, **Lua Script, scheduler, Redis Pipeline** 을 통해 저장한 평점 평균 데이터를 확인할 수 있다.
- **리뷰 등록**
    - 공연을 예매한 사용자는 공연 시작 후에 리뷰를 등록할 수 있다.
- **리뷰 삭제**
    - 공연이 삭제되면 **Kafka**를 통해 비동기로 해당 공연의 리뷰와 리뷰 좋아요를 삭제한다.
- **리뷰 좋아요**
    - 로그인한 사용자는 리뷰에 좋아요를 누를 수 있습니다.

<br>

## ⚙️ 5. 개발 환경 및 적용 기술

### Backend
- **Spring Security** : 사용자 인증과 권한 부여를 안전하게 처리하기 위해 사용했습니다. <br>특히, JWT와 같은 토큰 기반 인증 방식을 통해 MSA 환경에서 각 서비스 간 인증 정보를 쉽게 관리할 수 있도록 했습니다. 
- **Querydsl** : 복잡한 쿼리를 동적이고 안전하게 작성하기 위해 선택했습니다. 특히, JPA를 사용할 때 복잡한 조건을 가진 쿼리나 동적 쿼리를 안전하고 간결하게 관리할 수 있었습니다. 

### DB
- **Redis** : Redis는 메모리 기반으로 동작하는 데이터 저장소이기 때문에 읽기/쓰기 성능이 매우 뛰어나기 때문에 적당한 캐싱 전략을 하여 DB의 IO 비용을 감소 시키기 위해 도입했습니다. 
- **Postgresql** : 타 RDBMS와 다르게 UUID 타입을 지원하기 때문에 PK 설정시, 보안성을 높일 수 있습니다. 또한 높은 연결성과 신뢰성을 제공할 수 있다.  

### Messaging
- **Kafka** : 마이크로 서비스 간의 비동기 통신을 위해 사용하였으며, 이벤트 기반 시스템을 구축하여 실시간으로 데이터를 처리하고 서비스 간의 독립성을 유지하기 위한 메시지 브로커 역할을 수행했습니다. 

### Tech
- **PortOne** : PortOne은 간편한 결제 시스템 연동을 지원하는 API로, 다양한 결제 수단과 결제 서비스(카드 결제, 모바일 결제 등)를 쉽게 통합할 수 있습니다 

### Monitoring
- **Prometheus** : Prometheus는 모니터링 및 경고 시스템으로서, 타임 시리즈 데이터를 수집 및 저장하는 데 특화되어 있습니다.
- **Grafana** : Prometheus를 통해 수집한 데이트를 시각화하여 실시간으로 시스템 상태를 확인할 수 있습니다. 유연한 대시보드를 통해 데이터를 쉽게 분석할 수 있습니다. 

### Test
- **Jmeter** : 애플리케이션의 성능을 측정하고, 부하 테스트를 통해 시스템의 안정성을 확인하기 위해 오픈 소스 성능 테스트 도구인 JMeter를 사용했습니다. 마이크로서비스 아키텍처에서 각 서비스가 고부하 상황에서도 안정적으로 동작하는지 테스트하기 위해 선택했습니다. 

### CI/CD
- **Git Actions** : GitHub Actions는 GitHub와 통합된 CI/CD 도구로, 코드가 변경될 때마다 자동으로 빌드, 테스트, 배포 파이프 라인을 실행할 수 있는 자동화된 워크플로우를 제공합니다. 
- **ECR** : ECR은 AWS에서 제공하는 관리형 Docker 이미지 레지스트리로, 도커 이미지를 안전하게 저장하고 관리할 수 있습니다. 그리고 EC2와 같은 AWS 서비스로 쉽게 배포할 수 있습니다. 이를 통해 배포 프로세스가 간소화되고 안전성을 보장할 수 있습니다. 
- **EC2** : EC2는 AWS에서 제공하는 확장 가능한 컴퓨팅 리소스로, 다양한 애플리케이션을 배포 및 실행할 수 있습니다. 백엔드 서비스와 도커 컨테이너를 실행할 안정적인 컴퓨팅 환경을 제공하기 위해 EC2를 사용했습니다. 유연한 리소스 관리와 확장성을 통해 서버 관리가 용이했습니다. 
- Docker : Docker는 컨테이너 기반 가상화 기술로, 애플리케이션을 독립적인 환경에서 실행할 수 있어 개발, 테스트, 배포가 일관되게 이루어지도록 도와줍니다. 각 서비스가 독립적인 환경에서 동작할 수 있도록 컨테이너화를 통해 배포를 용이하게 하고, 일관된 개발 및 운영 환경을 제공하기 위해 사용했습니다. 

<br>

## 💡 6. 기술적 의사결정
- [[리뷰] review 목록 조회 과정에서 user 서버로부터 nickname 목록 조회](https://github.com/TEAM21-2NE1/ticketing/wiki/%5B%EB%A6%AC%EB%B7%B0%5D-review-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-%EA%B3%BC%EC%A0%95%EC%97%90%EC%84%9C-user-%EC%84%9C%EB%B2%84%EB%A1%9C%EB%B6%80%ED%84%B0-nickname-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C)
- [[리뷰] 평균 평점 구할 때 적용한 기술적 의사 결정](https://github.com/TEAM21-2NE1/ticketing/wiki/%5B%EB%A6%AC%EB%B7%B0%5D-%ED%8F%89%EA%B7%A0-%ED%8F%89%EC%A0%90-%EA%B5%AC%ED%95%A0-%EB%95%8C-%EC%A0%81%EC%9A%A9%ED%95%9C-%EA%B8%B0%EC%88%A0%EC%A0%81-%EC%9D%98%EC%82%AC-%EA%B2%B0%EC%A0%95)
- [[예매] Redis를 이용한 예매 대기열 구현](https://github.com/TEAM21-2NE1/ticketing/wiki/%5B%EC%98%88%EB%A7%A4%5D-Redis%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%98%88%EB%A7%A4-%EB%8C%80%EA%B8%B0%EC%97%B4-%EA%B5%AC%ED%98%84)
- [[예매] Redis keyspace notification을 이용한 좌석 선택 제한시간 구현](https://github.com/TEAM21-2NE1/ticketing/wiki/%5B%EC%98%88%EB%A7%A4%5D-Redis-keyspace-notification%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%A2%8C%EC%84%9D-%EC%84%A0%ED%83%9D-%EC%A0%9C%ED%95%9C%EC%8B%9C%EA%B0%84-%EA%B5%AC%ED%98%84)

<br>

## 📌 7. 트러블슈팅
- [[TS] [리뷰] 리뷰 목록 조회 시 List 탐색으로 TPS 200 이하로 떨어지는 현상 발생](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5B%EB%A6%AC%EB%B7%B0%5D-%EB%A6%AC%EB%B7%B0-%EB%AA%A9%EB%A1%9D-%EC%A1%B0%ED%9A%8C-%EC%8B%9C-List-%ED%83%90%EC%83%89%EC%9C%BC%EB%A1%9C-TPS-200-%EC%9D%B4%ED%95%98%EB%A1%9C-%EB%96%A8%EC%96%B4%EC%A7%80%EB%8A%94-%ED%98%84%EC%83%81-%EB%B0%9C%EC%83%9D)
- [[TS] [공연] 좌석 데이터 개별 Insert query db 과도한 부하](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5B%EA%B3%B5%EC%97%B0%5D-%EC%A2%8C%EC%84%9D-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EA%B0%9C%EB%B3%84-Insert-query-db-%EA%B3%BC%EB%8F%84%ED%95%9C-%EB%B6%80%ED%95%98)
- [[TS] [security] 예외가 발생했을 때, 403 Forbidden 리턴](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5Bsecurity%5D-%EC%98%88%EC%99%B8%EA%B0%80-%EB%B0%9C%EC%83%9D%ED%96%88%EC%9D%84-%EB%95%8C,-403-Forbidden-%EB%A6%AC%ED%84%B4)
- [[TS] [예매] 대기열 처리 중 사용자 수 불일치](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5B%EC%98%88%EB%A7%A4%5D-%EB%8C%80%EA%B8%B0%EC%97%B4-%EC%B2%98%EB%A6%AC-%EC%A4%91-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%88%98-%EB%B6%88%EC%9D%BC%EC%B9%98)
- [[TS] [예매] 대기열 동시성](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5B%EC%98%88%EB%A7%A4%5D-%EB%8C%80%EA%B8%B0%EC%97%B4-%EB%8F%99%EC%8B%9C%EC%84%B1)
- [[TS] [예매] 대기열 부하 테스트](https://github.com/TEAM21-2NE1/ticketing/wiki/%5BTS%5D-%5B%EC%98%88%EB%A7%A4%5D-%EB%8C%80%EA%B8%B0%EC%97%B4-%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8)

<br>

## 👩🏻‍💻🧑🏻‍💻 8. CONTRIBUTORS
|팀원명	|포지션	| 담당	|깃허브 링크|
|---| ---	|---|---|
|김소이|	팀장<br>- 인증 및 인가<br>- CI/CD	|▶ 사용자 인증 및 인가<br>- user: 회원가입, 로그인 구현<br>- gateway에서 회원 인증 필터 구현, 각 서비스에 인가 구현<br>▶ CI/CD<br>- Github Actions를 통한 CI/CD 파이프라인 구축<br>- ECR에 도커 이미지를 저장하고, EC2에서 컨테이너로 실행<br>- RDS에 데이터베이스 구축	| https://github.com/soy9 |
|임수진|	부팀장<br>- 대기열<br>- 주문	|▶ 대기열<br>- Redis를 이용한 예매 대기열 구현<br>▶ 주문<br>- 주문 생성 구현<br>	| https://github.com/lsj104|
|박지안|	팀원<br>- 리뷰|	▶ 리뷰<br>- Redis Cache 적용하여 리뷰 CRUD 구현<br>- Lua Script, 스케줄링, Redis Pipeline을 이용하여 평점 평균 저장<br>- 공연 삭제 시 kafka를 이용하여 리뷰, 좋아요 삭제 구현 |	https://github.com/JianBBB
|엄도원 | 팀원<br>- 공연<br>- 결제<br>- 주문<br>- 모니터링|▶ 공연<br>- 공연장, 공연, 공연 좌석 CRUD 구현<br>- redis와 scheduler를 이용해 공연 랭킹 구현<br>▶ 주문<br>- 주문 RUD 구현<br>- redis를 이용해 티켓팅 좌석 선택 기능 구현<br>▶ 결제<br>- portOne 을 이용해 외부 결제 연동<br>- 결제 CRUD<br>▶ 모니터링<br>- prometheus, grafana 를 이용해 모니터링 구축	| https://github.com/dowonidaa|
