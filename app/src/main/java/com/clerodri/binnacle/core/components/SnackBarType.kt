package com.clerodri.binnacle.core.components

 sealed  class SnackBarType {
     data object Success : SnackBarType()
     data object Warning : SnackBarType()
     data object Error : SnackBarType()
     data object Info : SnackBarType()
}