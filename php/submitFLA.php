 <?php 

//input parameters: START
// if of forum where posts have to be loaded
  // $fbarchive_forum_id = 4; // example
$fbarchive_forum_id = 5;  
//input parameters: END


// note that multibyte support has to be enabled in PHP
echo '<p>Attempting to load...</p>';


define('IN_PHPBB', true);

// Include files
$phpbb_root_path = (defined('PHPBB_ROOT_PATH')) ? PHPBB_ROOT_PATH : './../';
$phpEx = substr(strrchr(__FILE__, '.'), 1);
include($phpbb_root_path . 'common.' . $phpEx);

// Start session management
$user->session_begin();
$auth->acl($user->data);
$user->setup();
// End session management

include($phpbb_root_path . 'includes/functions_posting.' . $phpEx);

//include data
include($phpbb_root_path . 'loadFLAFB/flaArchiveData.' . $phpEx);

$arrlength=count($allArchive);

$poll = $uid = $bitfield = $options = ''; 

for($x=0;$x<$arrlength;$x++) {
  $totalPostContent =  $allArchive[$x];
  //echo $totalPostContent[0];
  //prepare and post content
  $my_subject = utf8_normalize_nfc(substr($totalPostContent[0],0,55) . '...');  
  $my_text = utf8_normalize_nfc($totalPostContent[0]);
  generate_text_for_storage($my_subject, $uid, $bitfield, $options, false, false, false);
  generate_text_for_storage($my_text, $uid, $bitfield, $options, true, true, true);
  $data = array( 
    'forum_id'      => $fbarchive_forum_id,
    'icon_id'       => false,
    'topic_id'       => 21,
    'enable_bbcode'     => true,
    'enable_smilies'    => true,
    'enable_urls'       => true,
    'enable_sig'        => true,

    'message'       => $my_text,
    'message_md5'   => md5($my_text),
                
    'bbcode_bitfield'   => $bitfield,
    'bbcode_uid'        => $uid,

    'post_edit_locked'  => 1,
    'topic_title'       => $my_subject,
    'notify_set'        => false,
    'notify'            => false,
    'post_time'         => 0,
    'forum_name'        => 'blah blah',
    'enable_indexing'   => true,
  );
  submit_post('post', $my_subject, '', POST_NORMAL, $poll, $data);
  $topicIdAssigned = $data['topic_id']; // assigned automatically when posted
  //echo '<br>comments: ';
  //get post id
  $innerlength=count($totalPostContent);
  for($y=1;$y<$innerlength;$y++) {
    //echo $totalPostContent[$y];
    //prepare and post reply with obtained post id
    $my_subject = utf8_normalize_nfc(substr($totalPostContent[$y],0,55) . '...');  
    $my_text = utf8_normalize_nfc($totalPostContent[$y]);
    generate_text_for_storage($my_subject, $uid, $bitfield, $options, false, false, false);
    generate_text_for_storage($my_text, $uid, $bitfield, $options, true, true, true);
    $data = array( 
     'forum_id'      => $fbarchive_forum_id,
     'icon_id'       => false,
     'topic_id'       => $topicIdAssigned,
     'enable_bbcode'     => true,
     'enable_smilies'    => true,
     'enable_urls'       => true,
     'enable_sig'        => true,
 
     'message'       => $my_text,
     'message_md5'   => md5($my_text),
                 
     'bbcode_bitfield'   => $bitfield,
     'bbcode_uid'        => $uid,

     'post_edit_locked'  => 1,
     'topic_title'       => $my_subject,
     'notify_set'        => false,
     'notify'            => false,
     'post_time'         => 0,
     'forum_name'        => 'blah blah',
     'enable_indexing'   => true,
     );
    submit_post('reply', $my_subject, '', POST_NORMAL, $poll, $data);
    //finished posting reply
  }
  echo '<br>' . 'added (' . ($x + 1) . '/' . $arrlength . ')';
}



echo '<br> <p>Loaded all the data. Yay!!!</p>'; ?> 
