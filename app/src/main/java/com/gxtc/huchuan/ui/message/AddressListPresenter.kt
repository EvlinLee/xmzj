package com.gxtc.huchuan.ui.message

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.PersonInfoBean
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.data.MessageSource
import com.gxtc.huchuan.data.PersonalInfoRepository
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoContract
import com.gxtc.huchuan.utils.RegexUtils
import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.URI

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/24.
 */
class AddressListPresenter(var mView: AddressListContract.View ?): AddressListContract.Presenter {

    private var mData: MessageSource = MessageRepository()
    private val mInfoData: PersonalInfoContract.Source = PersonalInfoRepository()

    private val phones = mutableListOf<PersonInfoBean>()
    private val searchSource = mutableListOf<PersonInfoBean>()
    private val cachePhones = mutableListOf<PersonInfoBean>()

    private val phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    private val simUri1 = "content://icc/adn"
    private val simUri2 = "content://sim/adn"

    init {
        mView?.setPresenter(this)
    }


    override fun readContacts(contentResolver: ContentResolver) {
        mView?.showLoad()

        val sub =
            Observable.create<MutableList<PersonInfoBean>> { sub: Subscriber<in MutableList<PersonInfoBean>>? ->
                            sub?.onNext(readPhoneContact(contentResolver, phonesUri))      //这里是读取手机上存的号码
                            Intent().let {
                                it.data = Uri.parse(simUri1)
                                sub?.onNext(readPhoneContact(contentResolver, it.data))    //这里是读取手机卡存的号码
                            }

                            Intent().let {
                                it.data = Uri.parse(simUri2)
                                sub?.onNext(readPhoneContact(contentResolver, it.data))    //这里是读取手机卡存的号码
                            }

                            sub?.onCompleted()
                        }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<MutableList<PersonInfoBean>>{

                        override fun onCompleted() {
                            if(phones.size != 0){
                                getFriendsByContacts()      //将联系人传到给服务器
                            }else{
                                mView?.showLoadFinish()
                                mView?.showEmpty()
                            }
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showLoadFinish()
                            mView?.showError("获取手机联系人失败")
                        }

                        override fun onNext(t: MutableList<PersonInfoBean>?) {
                            LogUtil.i("获取联系人个数  : " + t?.size)
                            t?.let {
                                phones.addAll(t)
                                searchSource.addAll(t)
                            }
                        }

                    })

            RxTaskHelper.getInstance().addTask(this, sub)
    }


    /**
     * 将联系人传给服务器 返回注册的信息
     */
    override fun getFriendsByContacts() {
        cachePhones.clear()
        cachePhones.addAll(getNextGroup())

        val token = UserManager.getInstance().token

        if(cachePhones.size != 0){
            val list = mutableListOf<String>()
            for(item in cachePhones){
                list.add(item.phones)
            }

            mData.getFriendsByContacts(token, list, object: ApiCallBack<MutableList<PersonInfoBean>>(){
                override fun onSuccess(data: MutableList<PersonInfoBean>?) {
                    mView?.showLoadFinish()

                    if(data == null || data.size == 0){
                        mView?.showFriendsData(cachePhones)
                        return
                    }
                    for(cache in cachePhones){
                        var flag = false
                        for(bean in data){
                            if(cache.phones.equals(bean.phones)){
                                flag = true
                            }
                        }

                        if(!flag){
                            data.add(cache)
                        }
                    }
                    mView?.showFriendsData(data)
                }

                override fun onError(errorCode: String?, message: String?) {
                    mView?.showLoadFinish()
                    mView?.showError(message)
                }
            })

        }else{
            mView?.showLoadFinish()
        }
    }


    override fun refreshData() {

    }


    override fun loadMoreData() {
        cachePhones.clear()
        cachePhones.addAll(getNextGroup())

        val token = UserManager.getInstance().token

        if(cachePhones.size != 0){
            val list = mutableListOf<String>()

            for(item in cachePhones){
                list.add(item.phones)
            }

            mData.getFriendsByContacts(token, list, object: ApiCallBack<MutableList<PersonInfoBean>>(){
                override fun onSuccess(data: MutableList<PersonInfoBean>?) {
                    mView?.showLoadFinish()

                    if(data == null || data.size == 0){
                        mView?.showLoadMoreData(cachePhones)
                        return
                    }
                    for(cache in cachePhones){
                        var flag = false
                        for(bean in data){
                            if(cache.phones.equals(bean.phones)){
                                flag = true
                            }
                        }

                        if(!flag){
                            data.add(cache)
                        }
                    }
                    mView?.showLoadMoreData(data)
                }

                override fun onError(errorCode: String?, message: String?) {
                    mView?.showLoadMoreData(null)
                    mView?.showError(message)
                }
            })

        }else{
            mView?.showNoLoadMore()
        }
    }


    override fun searchData(key: String) {
        val token = UserManager.getInstance().token
        val list = mutableListOf<String>()

        cachePhones.clear()
        cachePhones.addAll( searchSource.filter { it.phones.contains(key) || it.name.contains(key) } )
        cachePhones.mapTo(list){ it.phones }

        if(list.isNotEmpty()){
            mData.getFriendsByContacts(token, list, object: ApiCallBack<MutableList<PersonInfoBean>>(){
                override fun onSuccess(data: MutableList<PersonInfoBean>?) {
                    mView?.showLoadFinish()

                    if(data == null || data.size == 0){
                        mView?.showSearchResult(cachePhones)
                        return
                    }
                    for(cache in cachePhones){
                        var flag = false
                        for(bean in data){
                            if(cache.phones == bean.phones){
                                flag = true
                            }
                        }

                        if(!flag){
                            data.add(cache)
                        }
                    }
                    mView?.showSearchResult(data)
                }

                override fun onError(errorCode: String?, message: String?) {
                    mView?.showLoadMoreData(null)
                    mView?.showError(message)
                }
            })

        }else{
            mView?.showError("暂无联系人")
        }
    }


    override fun folowUser(userCode: String) {
        val token = UserManager.getInstance().token
        mInfoData.setUserFocus(object : ApiCallBack<Any>() {
            override fun onSuccess(data: Any) {
                mView?.showFollowSuccess()
            }

            override fun onError(errorCode: String, message: String) {
                ToastUtil.showShort(MyApplication.getInstance()!!, message)
            }
        }, token, "3", userCode)
    }


    override fun applyFriends(userCode: String,message:String) {
        val token = UserManager.getInstance().token
        mInfoData.applyFriends(object : ApiCallBack<Any>() {
            override fun onSuccess(data: Any) {
                mView?.showApplySuccess()
            }

            override fun onError(errorCode: String, message: String) {
                ToastUtil.showShort(MyApplication.getInstance(), message)
            }
        }, token, "3", userCode,message)
    }


    /**
     * 获取手机上的联系人信息
     */
    private var temp = 0
    private fun readPhoneContact(contentResolver: ContentResolver, uri: Uri): MutableList<PersonInfoBean>{
        val list = mutableListOf<PersonInfoBean>()

        var cursor: Cursor? = null
        try {
            // 查询联系人数据
            cursor = contentResolver.query(uri , null, null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))                 // 获取联系人手机号
                    val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))      // 获取联系人姓名

                    val parseNum = parseNumber(number)
                    if(parseNum.isNotEmpty()){
                        val bean = PersonInfoBean()
                        bean.phones = parseNum
                        bean.name = displayName
                        list.add(bean)
                    }

                    LogUtil.i("readPhoneContact  : " + (++temp))
                }
            }

            return list

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }

        return list
    }


    /**
     * 处理下手机号码  有的会＋86   如果是固话就过滤掉
     */
    private fun parseNumber(number: String?): String{
        var newNumber = number
        if(!TextUtils.isEmpty(newNumber)){

            //处理国际区号
            if(newNumber!!.startsWith("+86")){
                newNumber = newNumber.substring("+86".length, newNumber.length)
            }

            //处理有－号
            if(newNumber.contains("-")){
                val arr = newNumber.split("-")
                newNumber = ""
                for (s in arr){
                    newNumber += s
                }
            }

            //过滤掉固定电话
            if(!RegexUtils.isMobileExact(newNumber)){
                return ""
            }

            val selfPhone = UserManager.getInstance().phone
            if(newNumber.equals(selfPhone)){
                return ""
            }

            return newNumber
        }

        return ""
    }


    /**
     * 获取下一批联系人  每次15个
     */
    private fun getNextGroup(): MutableList<PersonInfoBean>{
        val list = mutableListOf<PersonInfoBean>()
        if(phones.size != 0){

            var maxSize = 15
            if(maxSize > phones.size){
                maxSize = phones.size - 1
            }

            (0 .. maxSize).mapTo(list) {
                phones.get(it)
            }

            phones.removeAll(list)
            LogUtil.i("phones.removeAll(list)   :  " + phones.size)
        }
        return list
    }

    override fun start() {

    }

    override fun destroy() {
        mView = null
        mData.destroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}