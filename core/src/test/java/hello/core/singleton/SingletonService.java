package hello.core.singleton;

public class SingletonService {
    // static으로 선언 시 class 레벨에 올라가기 때문에 딱 하나만 생성된다.
    private static final SingletonService instance = new SingletonService();

    public static SingletonService getInstance() {
        // 자바 실행 시 내부적으로 private static final SingletonService instance = new SingletonService(); 실행
        // 그래서 단 하나만 생성해둔다.
        // 이미 생성되어진 인스턴스를 반환한다.
        return instance;
    }

    private SingletonService() {
    }

    public void login() {
        System.out.println("싱글톤 객체 로직 호출");
    }

}
