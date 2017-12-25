# MyFirebaesChatApp

<h3>Description</h3>
<p>Android application uses Firebase to authenticate user, store user communication data (text, image), and display them for view. User functions are:
<ul>
  <li>Account Setting: Profile Image Setting, Status Text Update</li>
  <li>Friend Request: Send/Decline/Unfriend</li>
  </ul>
  Firebase Console provides services needed to establish connection from this application.
  </p>
    
<h3>Push Notifications</h3>
<p>Using Firebase Notification & Function, application has ability to send Push Notification to target users who receive new message, friend request.</p>
<h3>External Libraries</h3>
<p>External libraries are imported to modify profile images and provide ease of communication with Firebase Storage.</p>
<p><code>    compile 'de.hdodenhof:circleimageview:2.1.0'</code>

<code>     compile 'com.theartofdev.edmodo:android-image-cropper:2.6.0'</code>

<code>   compile 'com.iceteck.silicompressorr:silicompressor:2.0'</code>

<code>       compile 'com.squareup.picasso:picasso:2.5.2'</code>
<code>    compile 'id.zelory:compressor:2.0.0'</code>

<code>   compile 'com.squareup.okhttp:okhttp:2.5.0'</code>
</p>
<h3>Firebase Libraries</h3>
<p>Firebase provide the infrastructure needed to institute chatting service.</p>
<p><code>      implementation 'com.google.firebase:firebase-auth:11.0.4'</code>

<code>     implementation 'com.google.firebase:firebase-database:11.0.4'</code>

<code>    implementation 'com.google.firebase:firebase-storage:11.0.4'</code>

<code>    implementation 'com.google.firebase:firebase-messaging:11.0.4'</code>

<code>    compile 'com.firebaseui:firebase-ui:2.0.1'</code>

</code></p>
<h3>Android Libraries</h3>
<p><code>    implementation 'com.android.support:design:27.0.2'</code>

<code>implementation 'com.android.support:design:27.0.2'</code>

<code>apply plugin: 'com.google.gms.google-services'</code>
</p>
