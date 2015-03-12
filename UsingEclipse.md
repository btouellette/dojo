# Introduction #

svn:ignore should be set up throughout the repo properly already to ignore anything Eclipse will make.

Checkout a copy of the repo locally however you want. Start Eclipse and create a new project. Name it something and point it at the dojo root directory.

Now adjust so Eclipse doesn't explode on the hidden files in the checkout
http://www.damonkohler.com/2009/07/make-eclipse-ignore-svn-directories.html

At this point Eclipse has probably totally f\*cked up your repo. Run svn up and then svn status. If you see "? (directory)" just delete the directory and svn up again until svn status doesn't print anything.

Now you get to fight with Subversion instead of Eclipse! It probably won't restore the deleted directories correctly. If you deleted a directory and a base svn up isn't restoring it (I had to delete classes) then do a "svn up (directory)" to restore it directly.

Also occasionally other people's commits will bug out Eclipse and it'll tell you that it can't resolve Card/StoredCard and things to a type. DON'T PANIC! Find your towel and take a few deep breaths. Just edit any file and resave it to trigger a rebuild and it should be fine.

STEP BY STEP
```
govtpiggy: checkout a fresh version
SolusAeternum: from which point
SolusAeternum: in the directory
govtpiggy: from google code
SolusAeternum: yeah
SolusAeternum: checkout to where though
govtpiggy: and checkout from /trunk/
govtpiggy: to your desktop is fine
SolusAeternum: https://dojo.googlecode.com/svn/trunk/src/dojo/
SolusAeternum: oh from trunk
govtpiggy: lemme know once you got that
SolusAeternum: ok done
govtpiggy: go back to eclipse
govtpiggy: file->new->new java project
SolusAeternum: wait
SolusAeternum: nvm found it
govtpiggy: project name:  dojo
govtpiggy: create project from existing source
SolusAeternum: location?
govtpiggy: Desktop\dojo
govtpiggy: then Next
govtpiggy: default output folder: dojo/classes
govtpiggy: right click on images/High Res and configure inclusion/exclusion patterns
govtpiggy: add exclusion pattern **/.svn*
SolusAeternum: ok
govtpiggy: now back to where we were
govtpiggy: right click on images/High Res and remove from build path
govtpiggy: make sure default output folder is still dojo/classes
SolusAeternum: ok
govtpiggy: now Finish
govtpiggy: hit the big Run play button
govtpiggy: select Java Application
govtpiggy: and it should work
```

Step by Step 2013
<pre>
Download and install the latest version of the JDK<br>
Download Eclipse for Java developers - latest version<br>
Extract to folder where it'll live (I used My Documents)<br>
Use default workspace location<br>
Help -> Eclipse Marketplace<br>
Click over to popular and install Subclipse (with all options)<br>
Accept all EULAs and install<br>
Within the package explorer (the panel on the left if you're in the java developer perspective), right click to get the context menu and go to 'New' and 'Other...'<br>
Expand the SVN folder and select 'Checkout Projects from SVN' and select Next<br>
Select the Create a new repository location and select Next<br>
The URL will be: https://dojo.googlecode.com/svn<br>
At this point you'll either proceed to the "Check Out As" screen or get an error. If you're having issues connecting and may be behind a corporate firewall using a proxy server you'll need to configure those values: In %AppData%\Roaming\Subversion there is a file "servers", add appropriate http-proxy-host and http-proxy-port entries under the [global] section<br>
Select "trunk" then Finish<br>
Select Java Project<br>
Project name -> dojo -> Next<br>
Change default output folder to dojo/classes<br>
Project should now build and launch appropriately<br>
</pre>