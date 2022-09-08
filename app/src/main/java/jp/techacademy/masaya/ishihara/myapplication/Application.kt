package jp.techacademy.masaya.ishihara.myapplication

class Application : android.app.Application() {

    companion object {
        lateinit var instance: Application private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        var title_a: String =""
    }
}