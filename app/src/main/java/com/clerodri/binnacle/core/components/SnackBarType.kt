package com.clerodri.binnacle.core.components

 sealed  class SnackBarType {
     object Success : SnackBarType()
     object Warning : SnackBarType()
     object Error : SnackBarType()
     object Info : SnackBarType()
}