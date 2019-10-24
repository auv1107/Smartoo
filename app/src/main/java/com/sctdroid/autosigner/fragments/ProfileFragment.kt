package com.sctdroid.autosigner.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.sctdroid.autosigner.R
import com.sctdroid.autosigner.activities.TimelineActivity_
import com.sctdroid.autosigner.presentation.ui.activities.FriendListActivity
import com.sctdroid.autosigner.presentation.ui.activities.SimpleListActivity_
import com.sctdroid.autosigner.utils.AccessTokenKeeper
import com.sctdroid.autosigner.utils.Constants
import com.sctdroid.autosigner.utils.GlideCircleTransform
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WeiboAuthListener
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.sina.weibo.sdk.exception.WeiboException
import com.sina.weibo.sdk.net.RequestListener
import com.sina.weibo.sdk.openapi.UsersAPI
import com.sina.weibo.sdk.openapi.models.User
import kotlinx.android.synthetic.main.fragment_profile.*
import timber.log.Timber

/**
 * Created by lixindong on 1/26/16.
 */
open class ProfileFragment : BaseNavigationFragment(), View.OnClickListener {

    private var mSsoHandler: SsoHandler? = null
    private var mAuthInfo: AuthInfo? = null
    private lateinit var mAccessToken: Oauth2AccessToken
    private var mUserApi: UsersAPI? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View) {
        mAccessToken = AccessTokenKeeper.readAccessToken(activity)
        if (mAccessToken.isSessionValid()) {
            login_container!!.visibility = View.GONE
            initProfile()
        }
        focus_count.setOnClickListener(this)
        follower_count.setOnClickListener(this)
        login_btn.setOnClickListener(this)
        logout_btn.setOnClickListener(this)
        message_container.setOnClickListener(this)
    }

    internal open fun hideLoginContainer() {
        login_container!!.visibility = View.GONE
    }

    internal open fun initProfile() {
        mUserApi = UsersAPI(activity, Constants.APP_KEY, mAccessToken)
        val uid: Long = mAccessToken.uid.toLong()
        mUserApi!!.show(uid, object : RequestListener {
            override fun onComplete(s: String?) {
                val user: User = User.parse(s)
                name!!.text = user.name
                introduction!!.text = user.description
                message_count!!.text = user.statuses_count.toString()
                focus_count!!.text = user.friends_count.toString()
                follower_count!!.text = user.followers_count.toString()
                activity?.runOnUiThread {
                    Glide.with(activity)
                            .load(user.avatar_large)
                            .transform(GlideCircleTransform(activity))
                            .into(avatar)
                }
            }

            override fun onWeiboException(e: WeiboException) {
                e.printStackTrace()
            }
        })
    }

    internal inner class AuthListener : WeiboAuthListener {
        override fun onComplete(values: Bundle) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values)
            if (mAccessToken.isSessionValid) {
                Toast.makeText(activity, mAccessToken.toString(), Toast.LENGTH_LONG).show()
                AccessTokenKeeper.writeAccessToken(activity, mAccessToken) //保存Token

                activity?.runOnUiThread {
                    initProfile()
                    hideLoginContainer()
                }
            } else {
                // 当您注册的应用程序签名不正确时,就会收到错误Code,请确保签名正确

                val code: String = values.getString("code", "")
                Toast.makeText(activity, "wrong code $code", Toast.LENGTH_LONG).show()
            }
        }

        override fun onWeiboException(e: WeiboException) {
            Toast.makeText(activity, "onWeiboException", Toast.LENGTH_LONG).show()
        }

        override fun onCancel() {}
    }

    companion object {
        private val TAG = ProfileFragment::class.java.simpleName
    }

    override fun onClick(v: View?) {
        when (v) {
            focus_count -> startActivity(Intent(activity, FriendListActivity::class.java))
            follower_count -> SimpleListActivity_.intent(activity).start()
            logout_btn -> {
                AccessTokenKeeper.clear(activity)
                login_container.visibility = View.VISIBLE
            }
            login_btn -> {
                mAuthInfo = AuthInfo(activity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE)
                mSsoHandler = SsoHandler(activity, mAuthInfo)
                mSsoHandler!!.authorizeWeb(AuthListener())
            }
            message_container -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_timelineFragment)
            }
        }
    }

    init {
        Timber.d("ProfileFragment")
    }
}