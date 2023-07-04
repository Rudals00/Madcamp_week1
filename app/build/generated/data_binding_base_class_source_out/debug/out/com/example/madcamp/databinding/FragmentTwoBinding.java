// Generated by view binder compiler. Do not edit!
package com.example.madcamp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.madcamp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentTwoBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button backButton;

  @NonNull
  public final RelativeLayout detailInfo;

  @NonNull
  public final RelativeLayout fragment2;

  @NonNull
  public final TextView leftText;

  @NonNull
  public final ImageView photo;

  @NonNull
  public final RecyclerView recyclerview;

  @NonNull
  public final TextView rightText;

  @NonNull
  public final Toolbar toolbar;

  private FragmentTwoBinding(@NonNull ConstraintLayout rootView, @NonNull Button backButton,
      @NonNull RelativeLayout detailInfo, @NonNull RelativeLayout fragment2,
      @NonNull TextView leftText, @NonNull ImageView photo, @NonNull RecyclerView recyclerview,
      @NonNull TextView rightText, @NonNull Toolbar toolbar) {
    this.rootView = rootView;
    this.backButton = backButton;
    this.detailInfo = detailInfo;
    this.fragment2 = fragment2;
    this.leftText = leftText;
    this.photo = photo;
    this.recyclerview = recyclerview;
    this.rightText = rightText;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentTwoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentTwoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_two, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentTwoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.back_button;
      Button backButton = ViewBindings.findChildViewById(rootView, id);
      if (backButton == null) {
        break missingId;
      }

      id = R.id.detail_info;
      RelativeLayout detailInfo = ViewBindings.findChildViewById(rootView, id);
      if (detailInfo == null) {
        break missingId;
      }

      id = R.id.fragment2;
      RelativeLayout fragment2 = ViewBindings.findChildViewById(rootView, id);
      if (fragment2 == null) {
        break missingId;
      }

      id = R.id.leftText;
      TextView leftText = ViewBindings.findChildViewById(rootView, id);
      if (leftText == null) {
        break missingId;
      }

      id = R.id.photo;
      ImageView photo = ViewBindings.findChildViewById(rootView, id);
      if (photo == null) {
        break missingId;
      }

      id = R.id.recyclerview;
      RecyclerView recyclerview = ViewBindings.findChildViewById(rootView, id);
      if (recyclerview == null) {
        break missingId;
      }

      id = R.id.rightText;
      TextView rightText = ViewBindings.findChildViewById(rootView, id);
      if (rightText == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      return new FragmentTwoBinding((ConstraintLayout) rootView, backButton, detailInfo, fragment2,
          leftText, photo, recyclerview, rightText, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
